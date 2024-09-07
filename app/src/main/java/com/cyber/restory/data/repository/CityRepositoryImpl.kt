package com.cyber.restory.data.repository

import android.util.Log
import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.model.CityFilterResponse
import com.cyber.restory.domain.repository.CityRepository
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val apiService: ApiClient
) : CityRepository {
    override suspend fun getCityFilters(): List<CityFilterResponse> {
        Log.d("CityRepositoryImpl", "API로부터 도시 필터 요청 시작")
        val filters = apiService.getCityFilters()
        Log.d("CityRepositoryImpl", "API 응답 받음: ${filters.size}개의 필터")
        return filters
    }
}