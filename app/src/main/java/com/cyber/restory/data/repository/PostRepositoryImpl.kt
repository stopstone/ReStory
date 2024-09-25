package com.cyber.restory.data.repository

import android.util.Log
import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostResponse
import com.cyber.restory.domain.repository.PostRepository
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiClient
) : PostRepository {

    override suspend fun getPosts(city: String?, type: String?, size: Int?, page: Int?): PostResponse {
        Log.d("PostRepository", "게시글 목록 조회 시작: city=$city, type=$type, size=$size, page=$page")
        return apiService.getPosts(city, type, size, page).also {
            Log.d("PostRepository", "게시글 목록 조회 완료: ${it.data.size}개의 게시글 반환됨")
        }
    }

    override suspend fun getPostDetail(id: Int): Post {
        Log.d("PostRepository", "게시글 상세 조회 시작: id=$id")
        return apiService.getPostDetail(id).also {
            Log.d("PostRepository", "게시글 상세 조회 완료: 제목='${it.title}'")
        }
    }

    override suspend fun getPostsByTag(tagId: Int, size: Int?, page: Int?): PostResponse {
        Log.d("PostRepository", "태그별 게시글 목록 조회 시작: tagId=$tagId, size=$size, page=$page")
        return apiService.getPostsByTag(tagId, size, page).also {
            Log.d("PostRepository", "태그별 게시글 목록 조회 완료: ${it.data.size}개의 게시글 반환됨")
        }
    }
}