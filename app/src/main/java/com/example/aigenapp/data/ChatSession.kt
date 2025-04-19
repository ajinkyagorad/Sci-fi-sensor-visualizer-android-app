package com.example.aigenapp.data

import java.util.Date

/**
 * Represents a chat session with its own messages and topic.
 */
data class ChatSession(
    val id: Long = System.currentTimeMillis(),
    var topic: String = "New Chat",
    val created: Date = Date(),
    val messages: MutableList<ChatMessage> = mutableListOf()
)
