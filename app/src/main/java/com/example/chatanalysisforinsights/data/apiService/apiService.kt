package com.example.chatanalysisforinsights.data.apiService

import android.content.Context
import android.net.Uri
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    @POST("v1/completions")
    suspend fun getChatInsights(
        @Header("Authorization") apiKey: String,
        @Body request: ChatGPTRequest
    ): ChatGPTResponse
}

data class ChatGPTRequest(
    val model: String = "gpt-4o-mini",
    val prompt: String,
    val max_tokens: Int = 1500,
    val temperature: Float = 0.7f
)

data class ChatGPTResponse(
    val choices: List<ChatGPTChoice>
)

data class ChatGPTChoice(
    val text: String
)

data class DashboardData(
    val sentiment: String,
    val positivityNegativityRatio: String,
    val dominantSpeaker: String,
    val passiveParticipants: List<String>,
    val replyFrequency: String,
    val timeGaps: String,
    val topics: List<String>
)

