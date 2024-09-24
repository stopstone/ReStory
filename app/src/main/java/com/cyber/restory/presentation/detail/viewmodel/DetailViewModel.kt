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
            try {
                Log.d("DetailViewModel", "게시글 상세 정보 요청 시작: id=$id")
                val post = getPostDetailUseCase(id)
                _postDetail.value = post
                Log.d("DetailViewModel", "게시글 상세 정보 요청 성공: ${post.title}")
                getNearbyPlaces(post)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "게시글 상세 정보 요청 실패: ${e.message}", e)
            }
        }
    }

    private fun getNearbyPlaces(post: Post) {
        viewModelScope.launch {
            try {
                Log.d("DetailViewModel", "주변 장소 정보 요청 시작: 위도=${post.latitude}, 경도=${post.longitude}, 타입=${post.type}")
                val nearbyPlaceItems = mutableListOf<NearbyPlaceItem>()

                val contentTypeIds = when (post.type) {
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
                    Log.d("DetailViewModel", "contentTypeId: $contentTypeId, 받은 장소 수: ${places.size}")

                    if (places.isNotEmpty()) {
                        nearbyPlaceItems.add(NearbyPlaceItem.Title(titleMap[contentTypeId] ?: "주변 추천 장소"))
                        nearbyPlaceItems.add(NearbyPlaceItem.PlaceList(places.take(5)))
                        Log.d("DetailViewModel", "추가된 장소 타입: ${titleMap[contentTypeId]}, 개수: ${places.take(5).size}")
                    }
                }

                if (nearbyPlaceItems.isEmpty()) {
                    Log.d("DetailViewModel", "주변 장소가 없습니다.")
                    nearbyPlaceItems.add(NearbyPlaceItem.Title("주변 추천 장소"))
                    nearbyPlaceItems.add(NearbyPlaceItem.PlaceList(emptyList()))
                }

                _nearbyPlaces.value = nearbyPlaceItems
                Log.d("DetailViewModel", "주변 장소 정보 요청 성공: ${nearbyPlaceItems.size}개 아이템")

                // 디버깅을 위한 로그 추가
                nearbyPlaceItems.forEachIndexed { index, item ->
                    when (item) {
                        is NearbyPlaceItem.Title -> Log.d("DetailViewModel", "Item $index: Title - ${item.title}")
                        is NearbyPlaceItem.PlaceList -> Log.d("DetailViewModel", "Item $index: PlaceList - ${item.places.size} places")
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "주변 장소 정보 요청 실패: ${e.message}", e)
            }
        }
    }
}