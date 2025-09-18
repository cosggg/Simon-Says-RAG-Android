package com.darrylbayliss.simonsays.domain

import com.darrylbayliss.simonsays.data.MediapipeRepository
import javax.inject.Inject

class CreateSimonSaysGame @Inject constructor(private val mediapipeRepository: MediapipeRepository) {
    suspend operator fun invoke(possibleResponses: List<String>): Boolean =
        mediapipeRepository.createGame(possibleResponses)
}