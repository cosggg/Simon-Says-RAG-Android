package com.darrylbayliss.simonsays.data

import android.util.Log
import com.darrylbayliss.simonsays.domain.Message
import com.google.ai.edge.localagents.rag.chains.ChainConfig
import com.google.ai.edge.localagents.rag.chains.RetrievalAndInferenceChain
import com.google.ai.edge.localagents.rag.retrieval.RetrievalConfig
import com.google.ai.edge.localagents.rag.retrieval.RetrievalRequest
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.optionals.getOrNull

@Singleton
class MediapipeLLMLocalDataSource @Inject constructor(
    private val chainConfig: ChainConfig<String>,
) {
    private var retrievalAndInferenceChain: RetrievalAndInferenceChain? = null

    suspend fun setup(gameResponses: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            Log.i(
                MediapipeLLMLocalDataSource::class.java.simpleName,
                "Converting text file into embeddings"
            )

            val finished = chainConfig.semanticMemory.getOrNull()
                ?.recordBatchedMemoryItems(ImmutableList.copyOf(gameResponses))?.get()


            retrievalAndInferenceChain = RetrievalAndInferenceChain(chainConfig)

            Log.i(
                MediapipeLLMLocalDataSource::class.java.simpleName,
                "Textfile converted into embeddings: $finished."
            )

            true
        }
    }

    suspend fun start(): String {
        return withContext(Dispatchers.IO) {

            Log.i(
                MediapipeLLMLocalDataSource::class.java.simpleName,
                "Starting Simon says"
            )

            generateSimonSaysResponse(prompt = "I'm ready to play Simon Says. Give me a task!")
        }
    }

    suspend fun sendMessage(message: Message): String {
        return withContext(Dispatchers.IO) {

            Log.i(
                MediapipeLLMLocalDataSource::class.java.simpleName,
                "Simon is thinking of a new task"
            )

            generateSimonSaysResponse(message.text)
        }
    }

    private fun generateSimonSaysResponse(prompt: String): String {
        val retrievalRequest =
            RetrievalRequest.create(
                prompt,
                RetrievalConfig.create(
                    50,
                    0.1f,
                    RetrievalConfig.TaskType.RETRIEVAL_QUERY
                )
            )
        return retrievalAndInferenceChain!!.invoke(retrievalRequest).get().text
    }

    fun isSetupComplete(): Boolean = retrievalAndInferenceChain != null
}
