package com.cyber.restory.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.domain.usecase.search.AddSearchUseCase
import com.cyber.restory.domain.usecase.search.ClearRecentSearchesUseCase
import com.cyber.restory.domain.usecase.search.DeleteSearchUseCase
import com.cyber.restory.domain.usecase.search.GetRecentSearchesUseCase
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
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase
) : ViewModel() {

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    init {
        viewModelScope.launch {
            getRecentSearchesUseCase().collect {
                _recentSearches.value = it
            }
        }
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
            Log.d("SearchViewModel", "검색 요청: 쿼리='$query'")
            runCatching {
//                searchPostsUseCase(search = query)
            }.onSuccess { response ->
                addSearch(query)  // 성공적인 검색 후 최근 검색어에 추가
            }.onFailure { error ->
                Log.e("SearchViewModel", "검색 실패: ${error.message}", error)
            }
        }
    }
}