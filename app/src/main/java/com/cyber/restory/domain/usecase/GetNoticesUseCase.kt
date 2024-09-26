package com.cyber.restory.domain.usecase

import com.cyber.restory.data.model.Notice
import com.cyber.restory.domain.repository.NoticeRepository
import javax.inject.Inject

class GetNoticesUseCase @Inject constructor(private val repository: NoticeRepository) {
    suspend operator fun invoke(): List<Notice> {
        return repository.getNotices()
    }
}