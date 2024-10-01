package com.cyber.restory.presentation.notice.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.restory.data.model.Notice
import com.cyber.restory.domain.usecase.GetNoticesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val getNoticesUseCase: GetNoticesUseCase
) : ViewModel() {
    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadNotices()
    }

    private fun loadNotices() {
        viewModelScope.launch {
            try {
                val result = getNoticesUseCase()
                Log.d("Notice", result.toString())
                _notices.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            }
        }
    }


    fun toggleNoticeExpansion(position: Int) {
        val currentList = _notices.value.toMutableList()
        val currentNotice = currentList[position]
        currentList[position] = currentNotice.copy(isExpanded = !currentNotice.isExpanded)
        _notices.value = currentList
    }
}