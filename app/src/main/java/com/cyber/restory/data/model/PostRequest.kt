package com.cyber.restory.data.model

data class PostRequest(
    val category: String,
    val size: Int,
    val page: Int
)