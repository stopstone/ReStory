package com.cyber.restory.presentation.place.list

import com.cyber.restory.data.model.PostImage

data class PostItem(
    val id: Int,
    val title: String,
    val type: String,
    val typeDesc: String,
    val summary: String,
    val content: String,
    val subContent: String,
    val city: String,
    val cityDesc: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val remark: String,
    val telephone: String,
    val duration: String,
    val holiday: String,
    val url: String,
    val postImages: List<PostImage>,
    val selectedLatitude: Double,
    val selectedLongitude: Double,
    val distance: Double
)