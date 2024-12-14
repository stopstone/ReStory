package com.cyber.restory.presentation.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.LocationBasedTourItem
import com.cyber.restory.data.model.NearbyPlaceItem
import com.cyber.restory.data.model.Post
import com.cyber.restory.domain.usecase.GetNearbyPlacesUseCase
import com.cyber.restory.domain.usecase.GetPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase
) : ViewModel() {
    private val _postDetail = MutableStateFlow<Post?>(null)
    val postDetail: StateFlow<Post?> = _postDetail.asStateFlow()

    private val _nearbyPlaces = MutableStateFlow<List<NearbyPlaceItem>>(emptyList())
    val nearbyPlaces: StateFlow<List<NearbyPlaceItem>> = _nearbyPlaces.asStateFlow()

    fun initializeWithPostId(id: Int) {
        viewModelScope.launch {
            runCatching {
                val post = getPostDetailUseCase(id)
                _postDetail.value = post
                getNearbyPlaces(post)
            }
        }
    }

    private fun getNearbyPlaces(post: Post) {
        viewModelScope.launch {
            try {
                val nearbyPlaceItems = mutableListOf<NearbyPlaceItem>()
                val contentTypeIds = when (post.typeDesc) {
                    "카페" -> listOf("12")  // 관광지
                    "숙박" -> listOf("39", "12")  // 맛집, 관광지
                    "문화공간" -> listOf("39")  // 맛집
                    "체험" -> listOf("28")  // 레저
                    else -> listOf("12")  // 기본적으로 관광지
                }

                val titleMap = mapOf(
                    "12" to "주변 관광지 추천",
                    "39" to "주변 맛집 추천",
                    "28" to "주변 레저 시설 추천"
                )

                for (contentTypeId in contentTypeIds) {
                    val places = getNearbyPlacesUseCase(post.latitude, post.longitude, contentTypeId)
                        .sortedBy { it.dist.toDoubleOrNull() ?: Double.MAX_VALUE }  // 거리순 정렬

                    if (places.isNotEmpty()) {
                        nearbyPlaceItems.add(NearbyPlaceItem.Title(titleMap[contentTypeId] ?: "주변 추천 장소"))
                        nearbyPlaceItems.add(NearbyPlaceItem.PlaceList(places.take(5)))
                    }
                }

                if (nearbyPlaceItems.isEmpty()) {
                    nearbyPlaceItems.add(NearbyPlaceItem.Title("주변 추천 장소"))
                    nearbyPlaceItems.add(NearbyPlaceItem.PlaceList(emptyList()))
                }

                _nearbyPlaces.value = nearbyPlaceItems
            } catch (e: Exception) {
                Log.e("DetailViewModel", "주변 장소 정보 요청 실패: ${e.message}", e)
            }
        }
    }
}