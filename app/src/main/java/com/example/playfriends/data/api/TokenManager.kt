package com.example.playfriends.data.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TokenManager {
    private const val PREF_NAME = "token_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_TOKEN_TYPE = "token_type"
    
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String, tokenType: String = "bearer") {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .putString(KEY_TOKEN_TYPE, tokenType)
            .apply()
        Log.d("TokenManager", "토큰 저장됨: $tokenType $token")
    }
    
    fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    fun getTokenType(): String {
        return prefs.getString(KEY_TOKEN_TYPE, "bearer") ?: "bearer"
    }
    
    fun getFullToken(): String? {
        val token = getToken()
        val type = getTokenType()
        return if (token != null) "$type $token" else null
    }
    
    fun clearToken() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_TOKEN_TYPE)
            .apply()
        Log.d("TokenManager", "토큰 삭제됨")
    }
    
    fun hasToken(): Boolean {
        return getToken() != null
    }
} 