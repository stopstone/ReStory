package com.cyber.restory.data.repository

import com.cyber.restory.data.api.ApiClient
import com.cyber.restory.data.model.Notice
import com.cyber.restory.domain.repository.NoticeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient
) : NoticeRepository {
    override suspend fun getNotices(): List<Notice> {
        return apiClient.getNotices().data.map { item ->
            Notice(
                id = item.id,
                title = item.title,
                content = item.content,
                date = item.createdAt,
                isExpanded = false
            )
        }
    }
}