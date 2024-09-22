package com.cyber.restory.data.repository

import com.cyber.restory.data.db.RecentSearchDao
import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val recentSearchDao: RecentSearchDao
) : SearchRepository {
    override fun getRecentSearches(): Flow<List<RecentSearch>> =
        recentSearchDao.getRecentSearches()

    override suspend fun addSearch(query: String) {
        val existingSearch = recentSearchDao.findSearchByQuery(query)
        if (existingSearch != null) {
            recentSearchDao.updateSearch(existingSearch.copy(timestamp = System.currentTimeMillis()))
        } else {
            recentSearchDao.insertSearch(
                RecentSearch(
                    query = query,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun deleteSearch(search: RecentSearch) {
        recentSearchDao.deleteSearch(search)
    }

    override suspend fun deleteAllSearches() {
        recentSearchDao.deleteAllSearches()
    }
}