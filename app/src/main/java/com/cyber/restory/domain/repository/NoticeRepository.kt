package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.Notice

interface NoticeRepository {
    suspend fun getNotices(): List<Notice>
}