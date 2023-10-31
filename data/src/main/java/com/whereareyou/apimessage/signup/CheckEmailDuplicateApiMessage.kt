package com.whereareyou.apimessage.signup

import com.google.gson.annotations.SerializedName

data class CheckEmailDuplicateResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("message")
    val message: String
)