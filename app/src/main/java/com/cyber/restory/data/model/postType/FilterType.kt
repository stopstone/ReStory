package com.cyber.restory.data.model.postType

data class FilterType(
    val code: String,
    val description: String,
    var isSelected: Boolean = false
)