package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.CityFilterResponse

interface CityRepository {
    suspend fun getCityFilters(): List<CityFilterResponse>
}
