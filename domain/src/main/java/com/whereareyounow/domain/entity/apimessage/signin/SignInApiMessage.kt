package com.whereareyounow.domain.entity.apimessage.signin

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("password")
    val password: String
)

data class SignInResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("memberId")
    val memberId: String
)