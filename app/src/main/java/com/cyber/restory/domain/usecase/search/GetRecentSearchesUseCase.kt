package com.cyber.restory.domain.usecase.search

import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchesUseCase @Inject constructor(private val repository: SearchRepository) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.getRecentSearches()
}