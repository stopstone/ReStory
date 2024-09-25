package com.cyber.restory.domain.usecase.search

import com.cyber.restory.data.model.TagsResponse
import com.cyber.restory.domain.repository.SearchRepository
import javax.inject.Inject

class GetRecommendedTagsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(): TagsResponse {
        return searchRepository.getRecommendedTags()
    }
}