package com.example.marketplace.Adapter

import android.util.Log
import com.example.marketplace.Models.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class MessageListener(
    private val productId: String? = null,
    private val senderId: String? = null,
    private val userId: String? = null
) {
    private lateinit var messageListener: ListenerRegistration
    private val TAG = "com.example.marketplace.Adapter.MessageListener"

    fun startListening() {
        // Initialize Firestore
        val firestore = FirebaseFirestore.getInstance()

        // Reference to the "messages" collection
        val messagesCollection = firestore.collection("messages")

        // Create a query to listen for chat messages
        val query = messagesCollection
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        // Start listening for chat messages
        messageListener = query.addSnapshotListener { snapshot: QuerySnapshot?, error ->
            if (error != null) {
                Log.e(TAG, "Error listening for chat messages: ${error.message}")
                return@addSnapshotListener
            }

            // Process the received messages
            snapshot?.let { querySnapshot ->
                val messages = querySnapshot.toObjects(ChatMessage::class.java)
                handleReceivedMessages(messages)
            }
        }
    }

    private fun handleReceivedMessages(messages: List<ChatMessage>) {
        // Process the received messages here
        // You can update your UI, display notifications, etc.
    }

    fun stopListening() {
        // Remove the message listener
        messageListener.remove()
    }
}
