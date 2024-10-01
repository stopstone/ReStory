package com.cyber.restory.data.model

data class NoticeResponse(
    val count: Int,
    val data: List<NoticeItem>
)

data class NoticeItem(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String,
    val updatedBy: String
)