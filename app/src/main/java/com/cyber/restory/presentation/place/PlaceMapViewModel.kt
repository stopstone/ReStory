package com.cyber.restory.presentation.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.postType.FilterType
import com.cyber.restory.domain.usecase.GetCityFiltersUseCase
import com.cyber.restory.domain.usecase.GetFilterTypesUseCase
import com.cyber.restory.domain.usecase.GetPostsUseCase
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaceMapViewModel @Inject constructor(
    private val getFilterTypesUseCase: GetFilterTypesUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getCityFiltersUseCase: GetCityFiltersUseCase
) : ViewModel() {

    private val _filterTypeList = MutableLiveData<List<FilterType>>()
    val filterTypeList: LiveData<List<FilterType>>
        get() = _filterTypeList

    private val _cityFilters = MutableStateFlow<List<Region>>(emptyList())
    val cityFilters: StateFlow<List<Region>> = _cityFilters.asStateFlow()

    private val _placeList = MutableLiveData<List<Post>>()
    val placeList: LiveData<List<Post>>
        get() = _placeList

    private var _setBottomSheetDataEvent = MutableLiveData<Event<List<Post>>>()
    val setBottomSheetDataEvent: LiveData<Event<List<Post>>>
        get() = _setBottomSheetDataEvent

    private var _filterCategoryChangeEvent = MutableLiveData<Event<FilterType?>>()
    val filterCategoryChangeEvent: LiveData<Event<FilterType?>>
        get() = _filterCategoryChangeEvent

    private var currentPage = 1
    private val pageSize = 10
    private var _selectedType = MutableLiveData<FilterType>()

    private val _selectedRegion = MutableLiveData<Region?>(null)
    val selectedRegion: LiveData<Region?>
        get() = _selectedRegion

    init {
        initFilter()
    }
    fun init() {
        fetchFilterData()
        fetchPlaceData()
        getCityFilters()
    }

    private fun initFilter() {
        _selectedType.value = FilterType(
            code = "ALL",
            description = "전체",
            isSelected = false
        )
    }
    /*
    * 재생공간 필터 타입 조회
    * */
    private fun fetchFilterData() {
        viewModelScope.launch() {
            _filterTypeList.value = getFilterTypesUseCase().map { item ->
                FilterType(
                    code = item.code,
                    description = item.description,
                    isSelected = false
                )
            }
        }
    }

    fun fetchPlaceData(city: String? = "", type: String? = "") {
        viewModelScope.launch {
            val posts = getPostsUseCase(city, if(type == "ALL")  "" else type, pageSize, currentPage).data
            _placeList.value = posts
        }
    }

    fun getSelectPlaceData(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            val selectData = withContext(Dispatchers.IO) {
                _placeList.value?.filter { data ->
                    data.latitude == lat && data.longitude == lon
                } ?: emptyList()
            }
            withContext(Dispatchers.Main) {
                _setBottomSheetDataEvent.value = Event(selectData)
            }
        }
    }

    private fun getCityFilters() {
        viewModelScope.launch {
            val filters = getCityFiltersUseCase()
            val regions = filters.map { Region(it.code, it.description, it.cnt) }
            _cityFilters.value = regions
        }
    }

    fun getFilterPlace(type: FilterType) {
        _filterTypeList.value?.forEach {
            it.isSelected = it.code == type.code && !type.isSelected
        }
        _selectedType.value = _filterTypeList.value?.firstOrNull { it.isSelected } ?: FilterType(
            code = "ALL",
            description = "전체",
            isSelected = false
        )
        _filterCategoryChangeEvent.value = Event(_selectedType.value)
    }

    fun setSelectedRegion(region: Region) {
        _selectedRegion.value = region
        fetchPlaceData(region.code, _selectedType.value?.code ?: "ALL")
    }
}