package com.cyber.restory.data.model

data class Notice(
    val title: String,
    val date: String,
    val content: String,
    val isExpanded: Boolean = false
)