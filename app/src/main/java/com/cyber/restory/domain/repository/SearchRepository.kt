package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.TagsResponse
import com.cyber.restory.data.model.entity.RecentSearch
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getRecentSearches(): Flow<List<RecentSearch>>
    suspend fun addSearch(query: String)
    suspend fun deleteSearch(search: RecentSearch)
    suspend fun deleteAllSearches()
    suspend fun getRecommendedTags(): TagsResponse
}