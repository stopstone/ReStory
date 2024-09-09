package com.cyber.restory.domain.usecase

import com.cyber.restory.data.model.FilterTypeResponse
import com.cyber.restory.domain.repository.FilterRepository
import javax.inject.Inject

class GetFilterTypesUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) {
    suspend operator fun invoke(): List<FilterTypeResponse> {
        return filterRepository.getFilterTypes()
    }
}