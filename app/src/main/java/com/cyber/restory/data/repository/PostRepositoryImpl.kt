package com.cyber.restory.data.repository

import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostRequest
import com.cyber.restory.data.model.PostResponse
import com.cyber.restory.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiClient
) : PostRepository {
    override suspend fun getPosts(type: String, size: Int, page: Int): PostResponse {
        return apiService.getPosts(type, size, page)
    }

    override suspend fun getPostDetail(id: Int): Post {
        return apiService.getPostDetail(id)
    }
}
