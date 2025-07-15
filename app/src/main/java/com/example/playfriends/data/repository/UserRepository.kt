package com.example.playfriends.data.repository

import com.example.playfriends.data.api.RetrofitClient
import com.example.playfriends.data.api.RetrofitClient.apiService
import com.example.playfriends.data.api.TokenManager
import com.example.playfriends.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import android.util.Log

class UserRepository {
    private val apiService = RetrofitClient.apiService
    private val gson = Gson()

    // 로그인
    suspend fun login(loginRequest: LoginRequest): Result<Token> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(loginRequest)
            if (response.isSuccessful) {
                val token = response.body()
                if (token != null) {
                    // 토큰을 TokenManager에 저장
                    TokenManager.saveToken(token.access_token, token.token_type)
                    Result.success(token)
                } else {
                    Result.failure(Exception("로그인에 실패했습니다. 서버 응답을 확인해주세요."))
                }
            } else {
                // HTTP 상태 코드에 따른 구체적인 에러 메시지
                val errorMessage = when (response.code()) {
                    401 -> "아이디 또는 비밀번호가 올바르지 않습니다."
                    404 -> "사용자를 찾을 수 없습니다."
                    422 -> "입력 정보가 올바르지 않습니다."
                    500 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    else -> "로그인에 실패했습니다. (오류 코드: ${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // 네트워크 오류 등
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> "네트워크 연결을 확인해주세요."
                e.message?.contains("timeout") == true -> "요청 시간이 초과되었습니다. 다시 시도해주세요."
                e.message?.contains("failed to connect") == true -> "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요."
                e.message?.contains("Connection refused") == true -> "서버 연결이 거부되었습니다. 서버 상태를 확인해주세요."
                else -> "로그인 중 오류가 발생했습니다: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    // 로그아웃
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                // 로그아웃 성공 시 토큰 삭제
                TokenManager.clearToken()
                Result.success(Unit)
            } else {
                Result.failure(Exception("로그아웃 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            // 네트워크 오류가 있어도 로컬 토큰은 삭제
            TokenManager.clearToken()
            Result.failure(e)
        }
    }

    // 회원가입
    suspend fun createUser(userCreate: UserCreate): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createUser(userCreate)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("회원가입에 실패했습니다. 서버 응답을 확인해주세요."))
                }
            } else {
                // 에러 응답 파싱
                val errorMessage = parseErrorResponse(response, response.code())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // 네트워크 오류 등
            val errorMessage = when {
                e.message?.contains("Unable to resolve host") == true -> "네트워크 연결을 확인해주세요."
                e.message?.contains("timeout") == true -> "요청 시간이 초과되었습니다. 다시 시도해주세요."
                else -> "회원가입 중 오류가 발생했습니다: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    // 에러 응답 파싱 헬퍼 함수
    private fun parseErrorResponse(response: retrofit2.Response<*>, statusCode: Int): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)

                // 백엔드에서 보내는 구체적인 에러 메시지 우선 사용
                when {
                    errorResponse.detail?.contains("already exists", ignoreCase = true) == true -> "이미 존재하는 ID입니다."
                    errorResponse.detail?.contains("duplicate", ignoreCase = true) == true -> "이미 존재하는 ID입니다."
                    errorResponse.message?.contains("already exists", ignoreCase = true) == true -> "이미 존재하는 ID입니다."
                    errorResponse.message?.contains("duplicate", ignoreCase = true) == true -> "이미 존재하는 ID입니다."
                    errorResponse.detail?.contains("password", ignoreCase = true) == true -> "비밀번호 형식이 올바르지 않습니다."
                    errorResponse.message?.contains("password", ignoreCase = true) == true -> "비밀번호 형식이 올바르지 않습니다."
                    !errorResponse.detail.isNullOrEmpty() -> errorResponse.detail
                    !errorResponse.message.isNullOrEmpty() -> errorResponse.message
                    !errorResponse.error.isNullOrEmpty() -> errorResponse.error
                    else -> getDefaultErrorMessage(statusCode)
                }
            } else {
                getDefaultErrorMessage(statusCode)
            }
        } catch (e: Exception) {
            // JSON 파싱 실패 시 기본 메시지 사용
            getDefaultErrorMessage(statusCode)
        }
    }

    // 기본 에러 메시지 반환
    private fun getDefaultErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "입력 정보가 올바르지 않습니다."
            409 -> "이미 존재하는 ID입니다."
            422 -> "입력 정보가 올바르지 않습니다."
            500 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            else -> "회원가입에 실패했습니다. (오류 코드: $statusCode)"
        }
    }

    // 현재 사용자 정보 조회
    suspend fun getCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("사용자 정보 조회 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사용자의 그룹 목록 조회
    suspend fun getUserGroups(userId: String): Result<GroupList> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserGroups(userId)
            if (response.isSuccessful) {
                val groupList = response.body()
                if (groupList != null) {
                    Result.success(groupList)
                } else {
                    Result.failure(Exception("그룹 목록 조회 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 참여
    suspend fun joinGroup(groupId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.joinGroup(groupId)
            val errorBodyStr = response.errorBody()?.string() ?: ""
            Log.d("JoinGroup", "code=${response.code()}, body=${response.body()}, error=$errorBodyStr")
            if (response.isSuccessful) {
                val groupInfo = apiService.getGroup(groupId)
                val groupName = groupInfo.body()?.groupname ?: "그룹"
                val message = "$groupName 그룹에 참여했습니다."
                Log.d("JoinGroup", "성공 메시지: $message")
                Result.success(message)
            } else {
                val errorMsg = when {
                    response.code() == 404 -> "존재하지 않는 초대코드입니다."
                    response.code() == 400 -> "이미 참여 중인 그룹입니다."
                    response.code() == 500 && errorBodyStr.contains("not found", ignoreCase = true) -> "존재하지 않는 그룹입니다."
                    response.code() == 500 -> "존재하지 않는 초대코드입니다."
                    else -> "그룹 참여에 실패했습니다."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("그룹 참여 중 오류가 발생했습니다."))
        }
    }

    // 사용자 정보 수정
    suspend fun updateUserMe(userUpdate: UserUpdate): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateUserMe(userUpdate)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("사용자 정보 수정 실패: 응답 없음"))
            } else {
                Result.failure(Exception("사용자 정보 수정 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 선호도 수정
    suspend fun updatePreferences(preferences: PreferencesUpdate): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updatePreferences(preferences)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("선호도 수정 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
