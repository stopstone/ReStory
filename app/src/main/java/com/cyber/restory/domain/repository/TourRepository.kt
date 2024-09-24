package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.LocationBasedTourResponse
import com.cyber.restory.presentation.custom.Region

interface TourRepository {
    suspend fun getGreenTourInfo(region: Region): GreenTourResponse
    suspend fun getLocationBasedTourInfo(
        numOfRows: Int,
        mapX: String,
        mapY: String,
        arrange: String = "O",
        radius: String,
        contentTypeId: String
    ): LocationBasedTourResponse
}