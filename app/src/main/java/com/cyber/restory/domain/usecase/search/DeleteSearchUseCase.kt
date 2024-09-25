package com.cyber.restory.domain.usecase.search

import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.domain.repository.SearchRepository
import javax.inject.Inject

class DeleteSearchUseCase @Inject constructor(private val repository: SearchRepository) {
    suspend operator fun invoke(search: RecentSearch) = repository.deleteSearch(search)
}