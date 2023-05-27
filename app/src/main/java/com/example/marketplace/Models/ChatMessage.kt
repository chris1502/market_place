package com.example.marketplace.Models

data class ChatMessage(
    val id: String = "", // Add the id property
    val senderId: String = "",
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val productId: String = ""
)




