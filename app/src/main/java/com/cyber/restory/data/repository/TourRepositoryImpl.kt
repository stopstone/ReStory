package com.cyber.restory.data.repository

import android.util.Log
import com.cyber.restory.data.api.TourApiService
import com.cyber.restory.data.api.URL
import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.LocationBasedTourResponse
import com.cyber.restory.data.model.RegionCode
import com.cyber.restory.domain.repository.TourRepository
import com.cyber.restory.presentation.custom.Region
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val tourApiService: TourApiService
) : TourRepository {
    override suspend fun getGreenTourInfo(region: Region): GreenTourResponse {
        val regionCode = RegionCode.fromDescription(region.name)?.publicApiCode
            ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")

        return tourApiService.getGreenTourInfo(
            numOfRows = 10,
            pageNo = 1,
            mobileOS = "AND",
            mobileApp = "Restory",
            areaCode = regionCode,
            type = "json",
            serviceKey = URL.GREEN_TOUR_API
        )
    }

    override suspend fun getLocationBasedTourInfo(
        numOfRows: Int,
        mapX: String,
        mapY: String,
        radius: String,
        contentTypeId: String
    ): LocationBasedTourResponse {
        Log.d("TourRepositoryImpl", "위치 기반 관광 정보 요청 시작: 위도 = $mapY, 경도 = $mapX, 반경 = $radius, 컨텐츠 타입 = $contentTypeId")
        val response = tourApiService.getLocationBasedTourInfo(
            numOfRows = numOfRows,
            pageNo = 1,
            mobileOS = "AND",
            mobileApp = "Restory",
            type = "json",
            mapX = mapX,
            mapY = mapY,
            radius = radius,
            contentTypeId = contentTypeId,
            serviceKey = URL.GREEN_TOUR_API
        )
        Log.d("TourRepositoryImpl", "위치 기반 관광 정보 요청 완료: ${response.response.body.totalCount}개의 항목 수신")
        return response
    }
}