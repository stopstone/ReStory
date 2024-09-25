package com.cyber.restory.domain.usecase.search

import com.cyber.restory.domain.repository.SearchRepository
import javax.inject.Inject

class AddSearchUseCase @Inject constructor(private val repository: SearchRepository) {
    suspend operator fun invoke(query: String) = repository.addSearch(query)
}