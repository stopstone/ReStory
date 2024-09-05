package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.LocationBasedTourResponse
import com.cyber.restory.presentation.custom.Region

interface TourRepository {
    suspend fun getGreenTourInfo(region: Region): GreenTourResponse
    suspend fun getLocationBasedTourInfo(
        mapX: String,
        mapY: String,
        radius: String,
        contentTypeId: String
    ): LocationBasedTourResponse
}