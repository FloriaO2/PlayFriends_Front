package com.example.playfriends.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("detail")
    val detail: String? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("error")
    val error: String? = null,
    
    @SerializedName("errors")
    val errors: List<FieldError>? = null
)

data class FieldError(
    @SerializedName("field")
    val field: String? = null,
    
    @SerializedName("message")
    val message: String? = null
) 