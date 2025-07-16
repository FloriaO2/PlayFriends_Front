package com.example.playfriends.data.api

import android.os.Build
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 에뮬레이터인지 확인하는 함수 (개선된 버전)
    private fun isEmulator(): Boolean {
        val isEmulator = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Build.MODEL.contains("sdk_gphone")
                || Build.MODEL.contains("Pixel")
                || Build.MODEL.contains("Android SDK built for x86_64"))
        
        // 디버그 로그 출력
        Log.d("RetrofitClient", "=== 에뮬레이터 감지 정보 ===")
        Log.d("RetrofitClient", "FINGERPRINT: ${Build.FINGERPRINT}")
        Log.d("RetrofitClient", "MODEL: ${Build.MODEL}")
        Log.d("RetrofitClient", "MANUFACTURER: ${Build.MANUFACTURER}")
        Log.d("RetrofitClient", "BRAND: ${Build.BRAND}")
        Log.d("RetrofitClient", "DEVICE: ${Build.DEVICE}")
        Log.d("RetrofitClient", "PRODUCT: ${Build.PRODUCT}")
        Log.d("RetrofitClient", "isEmulator: $isEmulator")
        
        return isEmulator
    }
    
    // 동적으로 BASE_URL 생성
    private fun getBaseUrl(): String {
        val baseUrl = if (isEmulator()) {
            // 에뮬레이터일 때는 10.0.2.2 사용 (호스트 머신의 localhost)
            "https://playfriends-backend-432865170811.us-central1.run.app/"
        } else {
            // 실제 기기일 때는 192.249.27.32 사용
            "https://playfriends-backend-432865170811.us-central1.run.app/"
        }
        
        Log.d("RetrofitClient", "선택된 BASE_URL: $baseUrl")
        return baseUrl
    }
    
    // 토큰을 자동으로 헤더에 추가하는 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = TokenManager.getFullToken()
        
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
        } else {
            originalRequest
        }
        
        Log.d("RetrofitClient", "요청 URL: ${newRequest.url}")
        Log.d("RetrofitClient", "Authorization 헤더: ${newRequest.header("Authorization")}")
        
        chain.proceed(newRequest)
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // 토큰 인터셉터를 먼저 추가
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(getBaseUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 