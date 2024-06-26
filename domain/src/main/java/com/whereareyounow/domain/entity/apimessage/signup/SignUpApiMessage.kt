package com.whereareyounow.domain.entity.apimessage.signup

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String
)