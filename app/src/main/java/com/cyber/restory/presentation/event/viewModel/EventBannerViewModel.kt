package com.cyber.restory.presentation.event.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.RegionCode
import com.cyber.restory.domain.usecase.GetCityFiltersUseCase
import com.cyber.restory.domain.usecase.GetGreenTourInfoUseCase
import com.cyber.restory.domain.usecase.GetLocationBasedTourInfoUseCase
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.event.adapter.TourItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentBannerPosition: Int = -1

    fun initializeWithSeoul(bannerPosition: Int) {
        currentBannerPosition = bannerPosition
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getCityFiltersWithCount()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getCityFiltersWithCount() {
        try {
            val filters = getCityFiltersUseCase()
            val regionsWithCount = filters.map { filter ->
                viewModelScope.async {
                    val count = when (currentBannerPosition) {
                        0 -> getGreenTourCount(Region(filter.code, filter.description))
                        1, 2 -> getLocationBasedTourCount(Region(filter.code, filter.description), if (currentBannerPosition == 1) "12" else "15")
                        else -> 0
                    }
                    Region(filter.code, filter.description, count)
                }
            }.map { it.await() }

            _cityFilters.value = regionsWithCount
            val seoul = _cityFilters.value.find { it.code == "SEOUL" }
            seoul?.let { setSelectedRegion(it) }
        } catch (e: Exception) {
            Log.e("EventBannerViewModel", "도시 필터와 카운트 가져오기 실패: ${e.message}")
        }
    }

    private suspend fun getGreenTourCount(region: Region): Int {
        return try {
            val response = getGreenTourInfoUseCase(region)
            val items = response.response.body.itemsWrapper?.items ?: emptyList()
            items.count { item ->
                item.summary.contains("http://", ignoreCase = true) ||
                        item.summary.contains("https://", ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("EventBannerViewModel", "녹색 관광 정보 카운트 가져오기 실패: ${e.message}")
            0
        }
    }

    private suspend fun getLocationBasedTourCount(region: Region, contentTypeId: String): Int {
        return try {
            val regionCode = RegionCode.fromDescription(region.name)
                ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")
            val response = getLocationBasedTourInfoUseCase(
                regionCode.longitude.toString(),
                regionCode.latitude.toString(),
                "50000",
                contentTypeId
            )
            response.response.body.items.item.size
        } catch (e: Exception) {
            0
        }
    }

    fun setSelectedRegion(region: Region) {
        _selectedRegion.value = region
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (currentBannerPosition) {
                    0 -> getGreenTourInfo(region)
                    1 -> getLocationBasedTourInfo(region, "12")  // 관광지
                    2 -> getLocationBasedTourInfo(region, "15")  // 축제공연행사
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getGreenTourInfo(region: Region) {
        viewModelScope.launch {
            try {
                val response = getGreenTourInfoUseCase(region)
                val items = response.response.body.itemsWrapper?.items

                if (items.isNullOrEmpty()) {
                    _tourItems.value = emptyList()
                } else {
                    val filteredItems = items.filter { item ->
                        item.summary.contains("http://", ignoreCase = true) ||
                                item.summary.contains("https://", ignoreCase = true)
                    }
                    _tourItems.value = filteredItems.map { TourItem.GreenTour(it) }
                }
            } catch (e: Exception) {
                // 공통화
                _tourItems.value = emptyList()
            }
        }
    }

    private fun getLocationBasedTourInfo(region: Region, contentTypeId: String) {
        viewModelScope.launch {
            try {
                val regionCode = RegionCode.fromDescription(region.name)
                    ?: throw IllegalArgumentException("잘못된 지역명: ${region.name}")

                // 공통화
                val response = getLocationBasedTourInfoUseCase(
                    regionCode.longitude.toString(),
                    regionCode.latitude.toString(),
                    "50000",  // 50km를 미터 단위로 표현
                    contentTypeId
                )
                _tourItems.value =
                    response.response.body.items.item.map { TourItem.LocationBasedTour(it) }
            } catch (e: Exception) {
                _tourItems.value = emptyList()
            }
        }
    }
}