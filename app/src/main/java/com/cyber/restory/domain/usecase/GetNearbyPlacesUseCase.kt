package com.cyber.restory.domain.usecase

import android.util.Log
import com.cyber.restory.data.model.LocationBasedTourItem
import com.cyber.restory.domain.repository.TourRepository
import javax.inject.Inject

class GetNearbyPlacesUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double, type: String): List<LocationBasedTourItem> {
        val contentTypeId = when (type) {
            "카페" -> "12,14" // 관광지, 문화시설
            "숙박" -> "39,12" // 음식점, 관광지
            "문화공간" -> "39" // 음식점
            "체험" -> "12,28" // 관광지, 레포츠
            else -> "12" // 기본적으로 관광지
        }

        Log.d("GetNearbyPlacesUseCase", "주변 장소 요청 시작: 위도=$latitude, 경도=$longitude, 타입=$type")
        val response = tourRepository.getLocationBasedTourInfo(
            numOfRows = 5,
            mapX = longitude.toString(),
            mapY = latitude.toString(),
            radius = "30000", // 3km
            contentTypeId = contentTypeId
        )

        return response.response.body.items.item.map { item ->
            item.copy(
                contentTypeId = item.contentTypeId,
                distance = calculateDistance(
                    latitude, longitude,
                    item.mapy.toDouble(), item.mapx.toDouble()
                )
            )
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // 지구의 반경 (미터)
        val φ1 = lat1 * Math.PI / 180
        val φ2 = lat2 * Math.PI / 180
        val Δφ = (lat2 - lat1) * Math.PI / 180
        val Δλ = (lon2 - lon1) * Math.PI / 180

        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c // 미터 단위의 거리
    }
}
