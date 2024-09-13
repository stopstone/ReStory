package com.cyber.restory.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.postType.FilterTypeResponse
import com.cyber.restory.data.model.Post
import com.cyber.restory.domain.usecase.GetFilterTypesUseCase
import com.cyber.restory.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFilterTypesUseCase: GetFilterTypesUseCase,
    private val getPostsUseCase: GetPostsUseCase,
) : ViewModel() {

    private val _filterTypes = MutableStateFlow<List<FilterTypeResponse>>(emptyList())
    val filterTypes: StateFlow<List<FilterTypeResponse>> = _filterTypes.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<FilterTypeResponse?>(null)
    val selectedFilterType: StateFlow<FilterTypeResponse?> = _selectedFilterType.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _currentThumbnailPost = MutableStateFlow<Post?>(null)
    val currentThumbnailPost: StateFlow<Post?> = _currentThumbnailPost.asStateFlow()

    private val _selectedThumbnailPosition = MutableStateFlow(0)
    val selectedThumbnailPosition: StateFlow<Int> = _selectedThumbnailPosition.asStateFlow()

    private var thumbnailRotationJob: Job? = null
    private var isLongPressed = false

    private var currentPage = 1
    private val pageSize = 10

    fun getFilterTypes() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "필터 타입 가져오기 시작")
                val types = getFilterTypesUseCase()
                _filterTypes.value = types
                Log.d("HomeViewModel", "가져온 필터 타입: ${types.joinToString { "코드: ${it.code}, 설명: ${it.description}" }}")
                if (types.isNotEmpty() && _selectedFilterType.value == null) {
                    selectFilterType(types.first())
                }
                Log.d("HomeViewModel", "필터 타입 가져오기 성공: ${types.size}개의 타입")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "필터 타입 가져오기 실패: ${e.message}")
            }
        }
    }

    fun selectFilterType(filterType: FilterTypeResponse) {
        Log.d("HomeViewModel", "필터 타입 선택: ${filterType.description}")
        _selectedFilterType.value = filterType
        getPosts(filterType.code, resetPage = true)
    }

    private fun getPosts(type: String, resetPage: Boolean = false) {
        if (resetPage) {
            currentPage = 1
        }

        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "게시글 요청 시작: 타입=$type, 페이지=$currentPage, 사이즈=$pageSize")

                val result = getPostsUseCase("", type, pageSize, currentPage)

                Log.d("HomeViewModel", "게시글 요청 성공: 총 ${result.count}개의 게시글")
                Log.d("HomeViewModel", "받은 게시글 목록: ${result.data.joinToString { it.title }}")

                if (resetPage) {
                    _posts.value = result.data
                    if (result.data.isNotEmpty()) {
                        setCurrentThumbnailPost(result.data.first())
                        _selectedThumbnailPosition.value = 0
                    }
                } else {
                    _posts.value += result.data
                }

                resetThumbnailSelectionAndTimer()
                currentPage++
            } catch (e: Exception) {
                Log.e("HomeViewModel", "게시글 요청 실패: ${e.message}")
            }
        }
    }

    private fun setCurrentThumbnailPost(post: Post) {
        Log.d("HomeViewModel", "현재 썸네일 포스트 설정: ${post.title}")
        _currentThumbnailPost.value = post
    }

    private fun resetThumbnailSelectionAndTimer() {
        Log.d("HomeViewModel", "썸네일 선택 및 타이머 리셋")
        thumbnailRotationJob?.cancel()
        startThumbnailRotation()
    }

    private fun startThumbnailRotation() {
        thumbnailRotationJob?.cancel()
        thumbnailRotationJob = viewModelScope.launch {
            while (isActive) {
                delay(3000)
                if (!isLongPressed) {
                    val currentTime = System.currentTimeMillis()
                    val currentPosition = _selectedThumbnailPosition.value
                    val nextPosition = (currentPosition + 1) % (_posts.value.size.coerceAtLeast(1))
                    _selectedThumbnailPosition.value = nextPosition
                    setCurrentThumbnailPost(_posts.value[nextPosition])
                    Log.d("HomeViewModel", "타이머 동작: 현재 시간 $currentTime, 다음 포지션 $nextPosition")
                }
            }
        }
    }

    fun selectThumbnail(position: Int) {
        Log.d("HomeViewModel", "썸네일 선택: 포지션 $position")
        _selectedThumbnailPosition.value = position
        setCurrentThumbnailPost(_posts.value[position])
        resetThumbnailSelectionAndTimer()  // 타이머 초기화 추가
    }

    fun setLongPressState(isLongPressed: Boolean) {
        this.isLongPressed = isLongPressed
        Log.d("HomeViewModel", "롱프레스 상태 변경: $isLongPressed")
    }
}