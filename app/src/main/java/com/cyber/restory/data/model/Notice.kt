package com.cyber.restory.data.model

data class Notice(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val isExpanded: Boolean = false
)