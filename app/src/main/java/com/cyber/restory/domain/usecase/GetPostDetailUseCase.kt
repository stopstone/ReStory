package com.cyber.restory.domain.usecase

import com.cyber.restory.data.model.Post
import com.cyber.restory.domain.repository.PostRepository
import javax.inject.Inject

class GetPostDetailUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(id: Int): Post {
        return postRepository.getPostDetail(id)
    }
}