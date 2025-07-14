package com.example.playfriends.data.model

// 사용자 모델
data class User(
    val _id: String,
    val userid: String,
    val username: String,
    val is_active: Boolean = true,
    val food_preferences: FoodPreferences = FoodPreferences(),
    val play_preferences: PlayPreferences = PlayPreferences()
)

// 사용자 생성 요청 모델
data class UserCreate(
    val userid: String,
    val username: String,
    val is_active: Boolean = true,
    val password: String
)

// 사용자 업데이트 모델
data class UserUpdate(
    val username: String? = null,
    val food_preferences: FoodPreferences? = null,
    val play_preferences: PlayPreferences? = null
)

// 로그인 요청 모델
data class LoginRequest(
    val userid: String,
    val password: String,
    val auto_login: Boolean = false
)

// 토큰 응답 모델
data class Token(
    val access_token: String,
    val token_type: String
)

// 메시지 응답 모델
data class Message(
    val message: String
) 