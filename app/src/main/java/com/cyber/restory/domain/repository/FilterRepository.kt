package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.postType.FilterTypeResponse

interface FilterRepository {
    suspend fun getFilterTypes(): List<FilterTypeResponse>
}