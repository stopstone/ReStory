package com.cyber.restory.presentation.place.list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.postType.FilterType
import com.cyber.restory.domain.usecase.GetCityFiltersUseCase
import com.cyber.restory.domain.usecase.GetFilterTypesUseCase
import com.cyber.restory.domain.usecase.GetPostsUseCase
import com.cyber.restory.presentation.custom.Region
import com.cyber.restory.presentation.place.list.PostItem
import com.cyber.restory.utils.Event
import com.cyber.restory.utils.MapUtils
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceListViewModel @Inject constructor(
    private val getFilterTypesUseCase: GetFilterTypesUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getCityFiltersUseCase: GetCityFiltersUseCase
) : ViewModel() {

    private val _filterTypeList = MutableLiveData<List<FilterType>>()
    val filterTypeList: LiveData<List<FilterType>>
        get() = _filterTypeList

    private val _cityFilters = MutableStateFlow<List<Region>>(emptyList())
    val cityFilters: StateFlow<List<Region>> = _cityFilters.asStateFlow()

    private val _placeList = MutableLiveData<List<PostItem>>()
    val placeList: LiveData<List<PostItem>>
        get() = _placeList

    private var _filterCategoryChangeEvent = MutableLiveData<Event<FilterType?>>()
    val filterCategoryChangeEvent: LiveData<Event<FilterType?>>
        get() = _filterCategoryChangeEvent

    private var currentPage = 1
    private val pageSize = 100
    private var _selectedType = MutableLiveData<FilterType>()

    private val _selectedRegion = MutableLiveData<Region?>(null)
    val selectedRegion: LiveData<Region?>
        get() = _selectedRegion

    private var _selectedCityEvent = MutableLiveData<Event<String>>()
    val selectedCityEvent: LiveData<Event<String>>
        get() = _selectedCityEvent


    init {
        initFilter()
    }
    fun init() {
        fetchFilterData()
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

    fun fetchPlaceData(city: String? = "", type: String? = "", pos: LatLng? = null) {
        viewModelScope.launch {
            val posts = getPostsUseCase(city, if(type == "ALL")  "" else type, pageSize, currentPage).data
            _placeList.value = posts.map { post ->
                PostItem(
                    id = post.id,
                    title = post.title,
                    type = post.type,
                    typeDesc = post.typeDesc,
                    summary = post.summary,
                    content = post.content,
                    subContent = post.subContent,
                    city = post.city,
                    cityDesc = post.cityDesc,
                    address = post.address,
                    latitude = post.latitude,
                    longitude = post.longitude,
                    remark = post.remark,
                    telephone = post.telephone,
                    duration = post.duration,
                    holiday = post.holiday,
                    url = post.url,
                    postImages = post.postImages,
                    selectedLatitude = pos?.latitude ?: MapUtils.getCoordinate(_selectedRegion.value).first,
                    selectedLongitude = pos?.longitude ?: MapUtils.getCoordinate(_selectedRegion.value).second,
                    distance = MapUtils.getDistance(
                        post.latitude,
                        post.longitude,
                        pos?.latitude ?: MapUtils.getCoordinate(_selectedRegion.value).first,
                        pos?.longitude ?: MapUtils.getCoordinate(_selectedRegion.value).second
                        )
                )
            }.sortedBy { item ->
                item.distance
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

    fun getSelectedRegion() {
        _selectedCityEvent.value = Event(_selectedRegion.value?.code ?: "ALL")
    }
}