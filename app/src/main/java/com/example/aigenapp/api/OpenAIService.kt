package com.example.aigenapp.api

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

class OpenAIService(apiKey: String) {
    private val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds)
    )

    fun streamChat(prompt: String): Flow<String> = flow {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        
        openAI.chatCompletion(chatCompletionRequest).choices.forEach { choice ->
            choice.message.content?.let { emit(it) }
        }
    }

    suspend fun generateImage(prompt: String): String {
        val imageResponse = openAI.imageURL(
            creation = ImageCreation(
                prompt = prompt,
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        return imageResponse.first().url
    }
}
