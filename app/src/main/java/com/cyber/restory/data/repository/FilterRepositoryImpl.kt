package com.cyber.restory.data.repository

import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.model.FilterTypeResponse
import com.cyber.restory.domain.repository.FilterRepository
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient
) : FilterRepository {
    override suspend fun getFilterTypes(): List<FilterTypeResponse> {
        return apiClient.getFilterTypes()
    }
}