package com.cyber.restory.presentation.event.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getCityFiltersUseCase: GetCityFiltersUseCase
) : ViewModel() {

    private val _cityFilters = MutableStateFlow<List<Region>>(emptyList())
    val cityFilters: StateFlow<List<Region>> = _cityFilters.asStateFlow()

    fun getCityFilters() {
        viewModelScope.launch {
            try {
                Log.d("EventBannerViewModel", "도시 필터 가져오기 시작")
                val filters = getCityFiltersUseCase()
                _cityFilters.value = filters.map { Region(it.code, it.description) }
                Log.d("EventBannerViewModel", "도시 필터 가져오기 성공: ${filters.size}개의 필터")
            } catch (e: Exception) {
                Log.e("EventBannerViewModel", "도시 필터 가져오기 실패: ${e.message}")
            }
        }
    }
}