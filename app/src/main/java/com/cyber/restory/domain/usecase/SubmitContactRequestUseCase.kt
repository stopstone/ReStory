package com.cyber.restory.domain.usecase

import com.cyber.restory.data.model.ContactRequest
import com.cyber.restory.domain.repository.ContactRepository
import javax.inject.Inject

class SubmitContactRequestUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(request: ContactRequest) {
        contactRepository.submitContactRequest(request)
    }
}