package com.whereareyou.domain.entity.signup

import com.google.gson.annotations.SerializedName

data class ResponseMessage(
    @SerializedName("message")
    val message: String
)
