package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostRequest
import com.cyber.restory.data.model.PostResponse

interface PostRepository {
    suspend fun getPosts(postRequest: PostRequest): PostResponse
    suspend fun getPostDetail(id: Int): Post
}