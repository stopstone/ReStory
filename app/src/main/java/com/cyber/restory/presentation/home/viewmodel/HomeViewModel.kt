package com.cyber.restory.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.FilterTypeResponse
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.PostRequest
import com.cyber.restory.domain.usecase.GetFilterTypesUseCase
import com.cyber.restory.domain.usecase.GetPostDetailUseCase
import com.cyber.restory.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFilterTypesUseCase: GetFilterTypesUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getPostDetailUseCase: GetPostDetailUseCase
) : ViewModel() {
    private val _filterTypes = MutableStateFlow<List<FilterTypeResponse>>(emptyList())
    val filterTypes: StateFlow<List<FilterTypeResponse>> = _filterTypes.asStateFlow()

    private val _selectedFilterType = MutableStateFlow<FilterTypeResponse?>(null)
    val selectedFilterType: StateFlow<FilterTypeResponse?> = _selectedFilterType.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    private val _currentThumbnailPost = MutableStateFlow<Post?>(null)
    val currentThumbnailPost: StateFlow<Post?> = _currentThumbnailPost.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10 // 한 페이지당 게시글 수

    fun getFilterTypes() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "필터 타입 가져오기 시작")
                val types = getFilterTypesUseCase()
                _filterTypes.value = types
                Log.d(
                    "HomeViewModel",
                    "가져온 필터 타입: ${types.joinToString { "코드: ${it.code}, 설명: ${it.description}" }}"
                )
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

                val result = getPostsUseCase(type, pageSize, currentPage)

                Log.d("HomeViewModel", "게시글 요청 성공: 총 ${result.count}개의 게시글")
                Log.d("HomeViewModel", "받은 게시글 목록: ${result.data.joinToString { it.title }}")

                if (resetPage) {
                    _posts.value = result.data
                } else {
                    _posts.value += result.data
                }

                if (_currentThumbnailPost.value == null && result.data.isNotEmpty()) {
                    setCurrentThumbnailPost(result.data.first())
                }

                currentPage++
            } catch (e: Exception) {
                Log.e("HomeViewModel", "게시글 요청 실패: ${e.message}")
            }
        }
    }

    fun getPostDetail(id: Int) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "게시글 상세 정보 요청 시작: id=$id")
                val post = getPostDetailUseCase(id)
                _selectedPost.value = post
                Log.d("HomeViewModel", "게시글 상세 정보 요청 성공: ${post.title}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "게시글 상세 정보 요청 실패: ${e.message}")
            }
        }
    }

    fun setCurrentThumbnailPost(post: Post) {
        _currentThumbnailPost.value = post
    }
}