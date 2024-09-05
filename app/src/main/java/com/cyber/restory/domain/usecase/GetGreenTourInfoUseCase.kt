package com.cyber.restory.domain.usecase

import android.util.Log
import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.RegionCode
import com.cyber.restory.domain.repository.TourRepository
import com.cyber.restory.presentation.custom.Region
import javax.inject.Inject

class GetGreenTourInfoUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(region: Region): GreenTourResponse {
        val regionCode = RegionCode.fromDescription(region.name)?.publicApiCode
            ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")

        Log.d("GetGreenTourInfoUseCase", "녹색 관광 정보 요청 실행: 지역 = ${region.name}, 코드 = $regionCode")

        val response = tourRepository.getGreenTourInfo(region)

        Log.d("GetGreenTourInfoUseCase", "녹색 관광 정보 요청 완료: " +
                "총 항목 수 = ${response.response.body.totalCount}, " +
                "수신된 항목 수 = ${response.response.body.itemsWrapper?.items?.size ?: 0}")

        if (response.response.body.totalCount == 0 || response.response.body.itemsWrapper?.items.isNullOrEmpty()) {
            Log.w("GetGreenTourInfoUseCase", "수신된 녹색 관광 정보가 없습니다.")
        }

        return response
    }
}