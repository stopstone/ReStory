package com.cyber.restory.presentation.event.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.GreenTourItem
import com.cyber.restory.data.model.LocationBasedTourItem
import com.cyber.restory.data.model.RegionCode
import com.cyber.restory.domain.usecase.GetCityFiltersUseCase
import com.cyber.restory.domain.usecase.GetGreenTourInfoUseCase
import com.cyber.restory.domain.usecase.GetLocationBasedTourInfoUseCase
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.event.adapter.TourItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventBannerViewModel @Inject constructor(
    private val getCityFiltersUseCase: GetCityFiltersUseCase,
    private val getGreenTourInfoUseCase: GetGreenTourInfoUseCase,
    private val getLocationBasedTourInfoUseCase: GetLocationBasedTourInfoUseCase
) : ViewModel() {

    private val _cityFilters = MutableStateFlow<List<Region>>(emptyList())
    val cityFilters: StateFlow<List<Region>> = _cityFilters.asStateFlow()

    private val _selectedRegion = MutableStateFlow<Region?>(null)
    val selectedRegion: StateFlow<Region?> = _selectedRegion.asStateFlow()

    private val _tourItems = MutableStateFlow<List<TourItem>>(emptyList())
    val tourItems: StateFlow<List<TourItem>> = _tourItems.asStateFlow()

    private var currentBannerPosition: Int = -1

    fun initializeWithSeoul(bannerPosition: Int) {
        Log.d("EventBannerViewModel", "서울로 초기화 시작: 배너 위치 = $bannerPosition")
        currentBannerPosition = bannerPosition
        viewModelScope.launch {
            getCityFilters()
            val seoul = Region("SEOUL", "서울")
            setSelectedRegion(seoul)
        }
    }

    private suspend fun getCityFilters() {
        try {
            Log.d("EventBannerViewModel", "도시 필터 가져오기 시작")
            val filters = getCityFiltersUseCase()
            val regions = filters.map { Region(it.code, it.description) }
            _cityFilters.value = regions
            Log.d("EventBannerViewModel", "도시 필터 가져오기 성공: ${regions.size}개의 필터")
        } catch (e: Exception) {
            Log.e("EventBannerViewModel", "도시 필터 가져오기 실패: ${e.message}")
        }
    }

    fun setSelectedRegion(region: Region) {
        Log.d("EventBannerViewModel", "선택된 지역 설정: ${region.name}")
        _selectedRegion.value = region
        when (currentBannerPosition) {
            0 -> getGreenTourInfo(region)
            1 -> getLocationBasedTourInfo(region, "12")  // 관광지
            2 -> getLocationBasedTourInfo(region, "15")  // 축제공연행사
        }
    }

    private fun getGreenTourInfo(region: Region) {
        viewModelScope.launch {
            try {
                Log.d("EventBannerViewModel", "녹색 관광 정보 요청 시작: 지역 = ${region.name}")
                val response = getGreenTourInfoUseCase(region)
                val items = response.response.body.itemsWrapper?.items

                Log.d("EventBannerViewModel", "녹색 관광 정보 응답 수신: 총 항목 수 = ${response.response.body.totalCount}, 수신된 항목 수 = ${items?.size ?: 0}")

                if (items.isNullOrEmpty()) {
                    Log.w("EventBannerViewModel", "수신된 녹색 관광 정보가 없습니다.")
                    _tourItems.value = emptyList()
                } else {
                    val filteredItems = items.filter { item ->
                        item.summary.contains("http://", ignoreCase = true) ||
                                item.summary.contains("https://", ignoreCase = true)
                    }
                    _tourItems.value = filteredItems.map { TourItem.GreenTour(it) }
                    Log.d("EventBannerViewModel", "녹색 관광 정보 필터링 완료: 원본 ${items.size}개 중 ${filteredItems.size}개 항목 선택")
                }

                Log.d("EventBannerViewModel", "녹색 관광 정보 처리 완료: 최종 ${_tourItems.value.size}개 항목")
            } catch (e: Exception) {
                Log.e("EventBannerViewModel", "녹색 관광 정보 요청 실패: ${e.message}", e)
                _tourItems.value = emptyList()
            }
        }
    }

    private fun getLocationBasedTourInfo(region: Region, contentTypeId: String) {
        viewModelScope.launch {
            try {
                Log.d("EventBannerViewModel", "위치 기반 관광 정보 요청 시작: 지역 = ${region.name}, 컨텐츠 타입 = $contentTypeId")
                val regionCode = RegionCode.fromDescription(region.name)
                    ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")

                val response = getLocationBasedTourInfoUseCase(
                    regionCode.longitude.toString(),
                    regionCode.latitude.toString(),
                    "50000",  // 50km를 미터 단위로 표현
                    contentTypeId
                )
                _tourItems.value = response.response.body.items.item.map { TourItem.LocationBasedTour(it) }
                Log.d("EventBannerViewModel", "위치 기반 관광 정보 요청 성공: ${_tourItems.value.size}개의 항목")
            } catch (e: Exception) {
                Log.e("EventBannerViewModel", "위치 기반 관광 정보 요청 실패: ${e.message}")
            }
        }
    }
}