package com.example.noteapp.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

object GeminiAiHelper {
    // ВАЖНО: Тук трябва да се постави вашият API ключ от Google AI Studio
    // https://aistudio.google.com/
    private const val API_KEY = "YOUR_API_KEY_HERE"

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun summarizeNote(content: String): String? {
        return try {
            val response = model.generateContent(
                content {
                    text("Summarize the following note content briefly: $content")
                }
            )
            response.text
        } catch (e: Exception) {
            null
        }
    }

    suspend fun improveNote(content: String): String? {
        return try {
            val response = model.generateContent(
                content {
                    text("Improve the grammar and style of the following text, keep it in the same language: $content")
                }
            )
            response.text
        } catch (e: Exception) {
            null
        }
    }
}
