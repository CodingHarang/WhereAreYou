package com.whereareyou.apimessage.schedule

import com.google.gson.annotations.SerializedName
import com.whereareyou.domain.entity.schedule.LocationInformation

data class GetLocationAddressResponse(
    @SerializedName("lastBuildData")
    val lastBuildData: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("start")
    val start: Int,
    @SerializedName("display")
    val display: Int,
    @SerializedName("items")
    val items: List<LocationInformation>
)