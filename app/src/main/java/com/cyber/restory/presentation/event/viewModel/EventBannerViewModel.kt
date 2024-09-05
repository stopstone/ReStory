package com.cyber.restory.presentation.event.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.api.GreenTourApiService
import com.cyber.restory.data.api.URL
import com.cyber.restory.data.model.GreenTourItem
import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.RegionCode
import com.cyber.restory.domain.usecase.GetCityFiltersUseCase
import com.cyber.restory.presentation.custom.Region
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventBannerViewModel @Inject constructor(
    private val getCityFiltersUseCase: GetCityFiltersUseCase,
    private val greenTourApiService: GreenTourApiService
) : ViewModel() {

    private val _cityFilters = MutableStateFlow<List<Region>>(emptyList())
    val cityFilters: StateFlow<List<Region>> = _cityFilters.asStateFlow()

    private val _greenTourInfo = MutableStateFlow<GreenTourResponse?>(null)
    val greenTourInfo: StateFlow<GreenTourResponse?> = _greenTourInfo.asStateFlow()

    private val _selectedRegion = MutableStateFlow<Region?>(null)
    val selectedRegion: StateFlow<Region?> = _selectedRegion.asStateFlow()

    private val _filteredGreenTourItems = MutableStateFlow<List<GreenTourItem>>(emptyList())
    val filteredGreenTourItems: StateFlow<List<GreenTourItem>> = _filteredGreenTourItems.asStateFlow()

    private fun filterGreenTourItems(items: List<GreenTourItem>) {
        _filteredGreenTourItems.value = items.filter { item ->
            item.summary.contains("http://", ignoreCase = true) ||
                    item.summary.contains("https://", ignoreCase = true)
        }
    }

    fun initializeWithSeoul() {
        viewModelScope.launch {
            getCityFilters()
            val seoul = Region("SEOUL", "서울")
            setSelectedRegion(seoul)
            getGreenTourInfo()
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
        _selectedRegion.value = region
    }

    fun getGreenTourInfo() {
        viewModelScope.launch {
            try {
                val region = _selectedRegion.value ?: return@launch
                Log.d("EventBannerViewModel", "녹색 관광 정보 요청 시작: 지역 = ${region.name}")
                val regionCode = RegionCode.fromDescription(region.name)?.publicApiCode
                    ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")

                Log.d("EventBannerViewModel", "매핑된 지역 코드: $regionCode")

                val response = greenTourApiService.getGreenTourInfo(
                    numOfRows = 10,
                    pageNo = 1,
                    mobileOS = "AND",
                    mobileApp = "Restory",
                    areaCode = regionCode,
                    type = "json",
                    serviceKey = URL.GREEN_TOUR_API
                )

                Log.d("EventBannerViewModel", "녹색 관광 정보 요청 성공: ${response.response.body.totalCount}개의 항목 수신")
                _greenTourInfo.value = response
                filterGreenTourItems(response.response.body.items.item)

            } catch (e: Exception) {
                Log.e("EventBannerViewModel", "녹색 관광 정보 요청 실패: ${e.message}")
            }
        }
    }
}