package com.darrylbayliss.simonsays.data

import com.darrylbayliss.simonsays.domain.Message
import javax.inject.Inject

class MediapipeRepository @Inject constructor(private val mediapipeLLMLocalDataSource: MediapipeLLMLocalDataSource) {

    suspend fun createGame(gameResponses: List<String>): Boolean =
        mediapipeLLMLocalDataSource.setup(gameResponses = gameResponses)

    suspend fun startGame(): Message {
        val message = mediapipeLLMLocalDataSource.start()
        return Message(
            text = message,
            isFromMe = false
        )
    }

    suspend fun sendMessage(message: Message): String {
        return mediapipeLLMLocalDataSource.sendMessage(message)
    }

    fun isGameSetup(): Boolean = mediapipeLLMLocalDataSource.isSetupComplete()
}
