package com.cyber.restory.data.api

import com.cyber.restory.data.model.CityFilterResponse
import com.cyber.restory.data.model.FilterTypeResponse
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostRequest
import com.cyber.restory.data.model.PostResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiClient {
    @GET("filters/type")
    suspend fun getFilterTypes(): List<FilterTypeResponse>

    @GET("posts")
    suspend fun getPosts(
        @Query("type") type: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): PostResponse

    @GET("posts/{id}")
    suspend fun getPostDetail(@Path("id") id: Int): Post

    @GET("filters/city")
    suspend fun getCityFilters(): List<CityFilterResponse>
}