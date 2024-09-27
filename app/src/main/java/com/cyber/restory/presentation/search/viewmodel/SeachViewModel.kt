package com.cyber.restory.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.entity.RecentSearch
import com.cyber.restory.data.model.Post
import com.cyber.restory.data.model.Tag
import com.cyber.restory.domain.usecase.search.*
import com.cyber.restory.domain.repository.PostRepository
import com.cyber.restory.domain.usecase.GetPostDetailUseCase
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
    private val getRecommendedTagsUseCase: GetRecommendedTagsUseCase,
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val postRepository: PostRepository
) : ViewModel() {
    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    private val _recommendedTags = MutableStateFlow<List<Tag>>(emptyList())
    val recommendedTags: StateFlow<List<Tag>> = _recommendedTags.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Post>>(emptyList())
    val searchResults: StateFlow<List<Post>> = _searchResults.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())

    private val _selectedPostDetail = MutableStateFlow<Post?>(null)
    val selectedPostDetail: StateFlow<Post?> = _selectedPostDetail.asStateFlow()

    init {
        Log.d("SearchViewModel", "SearchViewModel 초기화")
        viewModelScope.launch {
            getRecentSearchesUseCase().collect {
                _recentSearches.value = it
                Log.d("SearchViewModel", "최근 검색어 업데이트: ${it.size}개의 검색어")
            }
        }
        fetchRecommendedTags()
        fetchAllTags()
    }

    fun addSearch(query: String) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "검색어 추가: '$query'")
            addSearchUseCase(query)
        }
    }

    fun deleteSearch(search: RecentSearch) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "검색어 삭제: '${search.query}'")
            deleteSearchUseCase(search)
        }
    }

    fun clearAllSearches() {
        viewModelScope.launch {
            Log.d("SearchViewModel", "모든 검색어 삭제")
            clearRecentSearchesUseCase()
        }
    }

    fun searchPosts(query: String) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "게시글 검색 시작: 쿼리='$query'")
            runCatching {
                val matchingTag = findMatchingTag(query)
                if (matchingTag != null) {
                    Log.d("SearchViewModel", "일치하는 태그 발견: ID=${matchingTag.id}, 이름='${matchingTag.name}'")
                    postRepository.getPostsByTag(matchingTag.id, size = 100, page = 1)
                } else {
                    Log.d("SearchViewModel", "일치하는 태그 없음, 전체 게시글에서 검색")
                    postRepository.getPosts(city = query, type = query, size = 100, page = 1)
                }
            }.onSuccess { response ->
                _searchResults.value = response.data
                Log.d("SearchViewModel", "검색 성공: ${response.data.size}개의 게시글 검색됨")
                addSearch(query)
                Log.d("SearchViewModel", "최근 검색어에 '$query' 추가됨")
            }.onFailure { error ->
                Log.e("SearchViewModel", "검색 실패: ${error.message}")
                Log.e("SearchViewModel", "스택 트레이스: ${error.stackTraceToString()}")
                _searchResults.value = emptyList()
            }
        }
    }

    private fun findMatchingTag(query: String): Tag? {
        return _allTags.value.find { it.name.equals(query, ignoreCase = true) }
    }

    private fun fetchAllTags() {
        viewModelScope.launch {
            Log.d("SearchViewModel", "모든 태그 조회 시작")
            runCatching {
                getRecommendedTagsUseCase()  // 이 부분은 실제로 모든 태그를 가져오는 API로 변경해야 합니다.
            }.onSuccess { response ->
                _allTags.value = response.data
                Log.d("SearchViewModel", "모든 태그 조회 성공: ${response.data.size}개의 태그")
            }.onFailure { error ->
                Log.e("SearchViewModel", "모든 태그 조회 실패: ${error.message}")
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

                val tagsToUse = response.data.take(6)
                _recommendedTags.value = tagsToUse

                tagsToUse.forEachIndexed { index, tag ->
                    Log.d("SearchViewModel", "태그 ${index + 1}: ID=${tag.id}, 이름='${tag.name}'")
                }

                if (response.data.size > 6) {
                    Log.d("SearchViewModel", "주의: 서버에서 ${response.data.size}개의 태그를 반환했지만, 처음 6개만 사용됩니다.")
                }
            }.onFailure { error ->
                Log.e("SearchViewModel", "추천 태그 조회 실패: ${error.message}")
                Log.e("SearchViewModel", "스택 트레이스: ${error.stackTraceToString()}")
            }
        }
    }

    fun searchPostsByTag(tagId: Int) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "태그별 게시글 검색 시작: tagId=$tagId")
            try {
                val response = postRepository.getPostsByTag(tagId, size = 100, page = 1)
                _searchResults.value = response.data
                Log.d("SearchViewModel", "태그별 게시글 검색 완료: ${response.data.size}개의 게시글 검색됨")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "태그별 게시글 검색 실패: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }

    fun onPostItemClick(postId: Int) {
        viewModelScope.launch {
            try {
                val postDetail = getPostDetailUseCase(postId)
                _selectedPostDetail.value = postDetail
                Log.d("SearchViewModel", "Post detail fetched: $postDetail")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching post detail: ${e.message}")
                _selectedPostDetail.value = null
            }
        }
    }
}