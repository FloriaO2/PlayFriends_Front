package com.example.playfriends.data.model.enums

import com.google.gson.annotations.SerializedName

enum class FoodIngredient(val value: String) {
    @SerializedName("고기")
    MEAT("고기"),
    @SerializedName("채소")
    VEGETABLE("채소"),
    @SerializedName("생선")
    FISH("생선"),
    @SerializedName("우유")
    MILK("우유"),
    @SerializedName("계란")
    EGG("계란"),
    @SerializedName("밀가루")
    FLOUR("밀가루")
}

enum class FoodTaste(val value: String) {
    @SerializedName("매운")
    SPICY("매운"),
    @SerializedName("느끼한")
    GREASY("느끼한"),
    @SerializedName("단")
    SWEET("단"),
    @SerializedName("짠")
    SALTY("짠"),
    @SerializedName("쓴")
    BITTER("쓴"),
    @SerializedName("신")
    SOUR("신")
}

enum class FoodCookingMethod(val value: String) {
    @SerializedName("국물")
    SOUP("국물"),
    @SerializedName("구이")
    GRILL("구이"),
    @SerializedName("찜/찌개")
    STEAMED("찜/찌개"),
    @SerializedName("볶음")
    STIR_FRIED("볶음"),
    @SerializedName("튀김")
    FRIED("튀김"),
    @SerializedName("날것")
    RAW("날것"),
    @SerializedName("음료")
    LIQUID("음료")
}

enum class FoodCuisineType(val value: String) {
    @SerializedName("한식")
    KOREAN("한식"),
    @SerializedName("중식")
    CHINESE("중식"),
    @SerializedName("일식")
    JAPANESE("일식"),
    @SerializedName("양식")
    WESTERN("양식"),
    @SerializedName("동남아식")
    SOUTHEAST_ASIAN("동남아식")
}
