package com.cyber.restory.domain.usecase

import com.cyber.restory.data.model.CityFilterResponse
import com.cyber.restory.domain.repository.CityRepository
import javax.inject.Inject

class GetCityFiltersUseCase @Inject constructor(
    private val repository: CityRepository
) {
    suspend operator fun invoke(): List<CityFilterResponse> {
        return repository.getCityFilters()
    }
}