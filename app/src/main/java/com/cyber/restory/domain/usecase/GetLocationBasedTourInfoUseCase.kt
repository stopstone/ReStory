package com.cyber.restory.domain.usecase

import android.util.Log
import com.cyber.restory.data.model.LocationBasedTourResponse
import com.cyber.restory.domain.repository.TourRepository
import javax.inject.Inject

class GetLocationBasedTourInfoUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(
        mapX: String,
        mapY: String,
        radius: String,
        contentTypeId: String
    ): LocationBasedTourResponse {
        Log.d(
            "GetLocationBasedTourInfoUseCase",
            "위치 기반 관광 정보 요청 실행: 위도 = $mapY, 경도 = $mapX, 반경 = $radius, 컨텐츠 타입 = $contentTypeId"
        )
        val response =
            tourRepository.getLocationBasedTourInfo(10, mapX, mapY, radius, contentTypeId)
        Log.d(
            "GetLocationBasedTourInfoUseCase",
            "위치 기반 관광 정보 요청 완료: ${response.response.body.totalCount}개의 항목 수신"
        )
        return response
    }
}