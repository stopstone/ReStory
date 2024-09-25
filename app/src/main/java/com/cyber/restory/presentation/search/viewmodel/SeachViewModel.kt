package com.cyber.restory.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.Tag
import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.domain.usecase.search.AddSearchUseCase
import com.cyber.restory.domain.usecase.search.ClearRecentSearchesUseCase
import com.cyber.restory.domain.usecase.search.DeleteSearchUseCase
import com.cyber.restory.domain.usecase.search.GetRecentSearchesUseCase
import com.cyber.restory.domain.usecase.search.GetRecommendedTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val addSearchUseCase: AddSearchUseCase,
    private val deleteSearchUseCase: DeleteSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val getRecommendedTagsUseCase: GetRecommendedTagsUseCase
) : ViewModel() {

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()


    private val _recommendedTags = MutableStateFlow<List<Tag>>(emptyList())
    val recommendedTags: StateFlow<List<Tag>> = _recommendedTags.asStateFlow()

    init {
        viewModelScope.launch {
            getRecentSearchesUseCase().collect {
                _recentSearches.value = it
            }
        }
        fetchRecommendedTags()
    }

    fun addSearch(query: String) {
        viewModelScope.launch {
            addSearchUseCase(query)
        }
    }

    fun deleteSearch(search: RecentSearch) {
        viewModelScope.launch {
            deleteSearchUseCase(search)
        }
    }

    fun clearAllSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }

    fun searchPosts(query: String) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "검색 요청 시작: 쿼리='$query'")
            runCatching {
                // searchPostsUseCase(search = query)
                // 실제 검색 로직 구현 필요
                Log.d("SearchViewModel", "검색 로직 실행 중...")
            }.onSuccess { response ->
                Log.d("SearchViewModel", "검색 성공: 응답=$response")
                addSearch(query)
                Log.d("SearchViewModel", "최근 검색어에 '$query' 추가됨")
            }.onFailure { error ->
                Log.e("SearchViewModel", "검색 실패: ${error.message}", error)
                Log.e("SearchViewModel", "스택 트레이스: ${error.stackTraceToString()}")
            }
        }
    }

    private fun fetchRecommendedTags() {
        viewModelScope.launch {
            Log.d("SearchViewModel", "추천 태그 조회 시작")
            runCatching {
                getRecommendedTagsUseCase()
            }.onSuccess { response ->
                Log.d("SearchViewModel", "추천 태그 조회 성공:")
                Log.d("SearchViewModel", "응답 받은 태그 수: ${response.count}")
                Log.d("SearchViewModel", "실제 사용할 태그 수: 6")
                _recommendedTags.value = response.data

            }.onFailure { error ->
                Log.e("SearchViewModel", "추천 태그 조회 실패: ${error.message}")
                Log.e("SearchViewModel", "스택 트레이스: ${error.stackTraceToString()}")
            }
        }
    }
}