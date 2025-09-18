package com.darrylbayliss.simonsays.di

import android.content.Context
import com.google.ai.edge.localagents.rag.chains.ChainConfig
import com.google.ai.edge.localagents.rag.chains.RetrievalAndInferenceChain
import com.google.ai.edge.localagents.rag.memory.DefaultSemanticTextMemory
import com.google.ai.edge.localagents.rag.memory.SqliteVectorStore
import com.google.ai.edge.localagents.rag.models.GeckoEmbeddingModel
import com.google.ai.edge.localagents.rag.models.MediaPipeLlmBackend
import com.google.ai.edge.localagents.rag.prompt.PromptBuilder
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Optional
import javax.inject.Singleton


private const val GemmaModelPath = "/data/local/tmp/slm/gemma3-1B-it-int4.task"
private const val GeckoEmbedderPath = "/data/local/tmp/slm/gecko_256_f32.tflite"
private const val TokenizerModelPath = "/data/local/tmp/slm/sentencepiece.model"
private const val UseGpuForEmbeddings = true
private const val PromptTemplate: String = """
    You are Simon in a game of Simon Says.
    
    Your task is to ask the player to perform a task from the following list: {0}.
    
    Your response must only contain the task that the player must do.
    
    Your response must be based on the players request: {1}. 
    
    Do not ask the player to do the same thing twice.
    
    You must not ask the player to do anything that is dangerous, unethical or unlawful.
"""

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideLlmInferenceOptions(): LlmInferenceOptions {
        return LlmInferenceOptions.builder()
            .setModelPath(GemmaModelPath)
            .setPreferredBackend(LlmInference.Backend.CPU) // Change to GPU if you have a GPU powered device.
            .setMaxTokens(1200)
            .build()
    }

    @Provides
    fun provideLlmInferenceSessionOptions(): LlmInferenceSessionOptions {
        return LlmInferenceSessionOptions.builder()
            .setTemperature(0.6f)
            .setTopK(5000)
            .setTopP(1f)
            .build()
    }

    @Provides
    fun provideLlmInference(
        @ApplicationContext context: Context,
        llmInferenceOptions: LlmInferenceOptions
    ): LlmInference {
        return LlmInference.createFromOptions(
            context,
            llmInferenceOptions
        )
    }

    @Provides
    @Singleton
    fun provideMediaPipeLanguageModel(
        @ApplicationContext context: Context,
        languageModelOptions: LlmInferenceOptions,
        languageModelSessionOptions: LlmInferenceSessionOptions
    ): MediaPipeLlmBackend {
        val test = MediaPipeLlmBackend(
            context,
            languageModelOptions,
            languageModelSessionOptions
        )
        test.initialize().get()
        return test
    }

    @Provides
    fun provideGeckoEmbedder(): GeckoEmbeddingModel {
        return GeckoEmbeddingModel(
            GeckoEmbedderPath,
            Optional.of(TokenizerModelPath),
            UseGpuForEmbeddings,
        )
    }

    @Provides
    fun provideChainConfig(
        embedder: GeckoEmbeddingModel,
        mediaPipeLanguageModel: MediaPipeLlmBackend
    ): ChainConfig<String> {
        return ChainConfig.create(
            mediaPipeLanguageModel, PromptBuilder(PromptTemplate),
            DefaultSemanticTextMemory(
                SqliteVectorStore(768), embedder
            )
        )
    }

    @Provides
    fun provideRetrievalAndConfigChain(config: ChainConfig<String>): RetrievalAndInferenceChain {
        return RetrievalAndInferenceChain(config)
    }
}
