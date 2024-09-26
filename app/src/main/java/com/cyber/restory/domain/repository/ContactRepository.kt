package com.cyber.restory.domain.repository

import com.cyber.restory.data.model.ContactRequest

interface ContactRepository {
    suspend fun submitContactRequest(request: ContactRequest)
}