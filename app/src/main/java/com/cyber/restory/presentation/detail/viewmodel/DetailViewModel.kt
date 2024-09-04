package com.cyber.restory.presentation.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.Post
import com.cyber.restory.domain.usecase.GetPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase
): ViewModel() {
    private val _postDetail = MutableStateFlow<Post?>(null)
    val postDetail: StateFlow<Post?> = _postDetail.asStateFlow()

    fun getPostDetail(id: Int) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "게시글 상세 정보 요청 시작: id=$id")
                val post = getPostDetailUseCase(id)
                _postDetail.value = post
                Log.d("HomeViewModel", "게시글 상세 정보 요청 성공: ${post.title}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "게시글 상세 정보 요청 실패: ${e.message}")
            }
        }
    }

}