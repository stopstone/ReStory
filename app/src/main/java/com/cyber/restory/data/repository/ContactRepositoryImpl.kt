package com.cyber.restory.data.repository

import com.cyber.restory.data.model.ContactRequest
import com.cyber.restory.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    // 여기에 API 서비스
) : ContactRepository {
    override suspend fun submitContactRequest(request: ContactRequest) {
        // API가 구현되면 여기서 실제 네트워크 요청을 수행
    }
}