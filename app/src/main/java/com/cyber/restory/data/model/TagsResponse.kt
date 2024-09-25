package com.cyber.restory.data.model

data class TagsResponse(
    val count: Int,
    val data: List<Tag>
)

data class Tag(
    val id: Int,
    val name: String
)