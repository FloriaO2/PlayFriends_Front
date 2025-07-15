package com.example.playfriends.data.model

import com.example.playfriends.data.model.enums.*

// 음식 취향 관련 모델들
data class IngredientPreference(
    val name: FoodIngredient,
    val score: Float = 0.0f
)

data class TastePreference(
    val name: FoodTaste,
    val score: Float = 0.0f
)

data class CookingMethodPreference(
    val name: FoodCookingMethod,
    val score: Float = 0.0f
)

data class CuisineTypePreference(
    val name: FoodCuisineType,
    val score: Float = 0.0f
)

// 음식 취향 전체 모델
data class FoodPreferences(
    val ingredients: List<IngredientPreference> = emptyList(),
    val tastes: List<TastePreference> = emptyList(),
    val cooking_methods: List<CookingMethodPreference> = emptyList(),
    val cuisine_types: List<CuisineTypePreference> = emptyList()
)

// 놀이 취향 모델
data class PlayPreferences(
    val crowd_level: Float = 0.0f,        // 붐비는 정도 (-1: 조용, 1: 붐빔)
    val activeness_level: Float = 0.0f,   // 활동성 (-1: 관람형, 1: 체험형)
    val trend_level: Float = 0.0f,        // 유행 민감도 (-1: 비유행, 1: 유행)
    val planning_level: Float = 0.0f,     // 계획성 (-1: 즉흥, 1: 계획)
    val location_preference: Float = 0.0f, // 장소 (-1: 실외, 1: 실내)
    val vibe_level: Float = 0.0f          // 분위기 (-1: 안정, 1: 도파민 추구)
)

// 선호도 업데이트를 위한 모델
data class PreferencesUpdate(
    val food_preferences: FoodPreferences,
    val play_preferences: PlayPreferences
)
