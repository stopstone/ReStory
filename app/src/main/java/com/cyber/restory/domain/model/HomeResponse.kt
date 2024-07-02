package com.cyber.restory.domain.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val home: Home
)

data class Home(
    val banners: List<Banner>
)

data class Banner(
    val id: Int,
    val image: String,
    val title: String,
    val subtitle: String,
    val badge: Badge
)

data class Badge(
    val label: String,
    @SerializedName("background_color") val backgroundColor: String
)