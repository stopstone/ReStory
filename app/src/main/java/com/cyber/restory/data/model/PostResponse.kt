package com.cyber.restory.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PostResponse(
    val count: Int,
    val data: List<Post>
)

@Parcelize
data class Post(
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
    val postImages: List<PostImage>
) : Parcelable

@Parcelize
data class PostImage(
    val id: Int,
    val imageUrl: String,
    val description: String
) : Parcelable