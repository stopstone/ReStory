package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostResponse

interface PostRepository {
    suspend fun getPosts(city:String?, type: String?, size: Int?, page: Int?): PostResponse
    suspend fun getPostDetail(id: Int): Post
}