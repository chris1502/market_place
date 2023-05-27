package com.example.marketplace.Chat

import com.example.marketplace.Models.Product
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.Adapter.ChatMessageAdapter
import com.example.marketplace.Models.ChatMessage
import com.example.marketplace.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.Date


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var productNameListLayout: LinearLayout
    private lateinit var messageViewLayout: LinearLayout

    private val chatMessages: MutableList<ChatMessage> = mutableListOf()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var messagesCollection: CollectionReference
    private lateinit var product: Product

    private lateinit var senderId: String
    private lateinit var userId: String
    private lateinit var productId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatMessageAdapter = ChatMessageAdapter(chatMessages)
        chatRecyclerView.adapter = chatMessageAdapter

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        productNameListLayout = findViewById(R.id.productNameListLayout)

        // Retrieve the productId from the intent extras
        productId = intent.getStringExtra("productId").toString()
        userId = intent.getStringExtra("userId").toString() // User ID of the person pressing the "Chat with Seller" button
        senderId = intent.getStringExtra("senderId").toString() // Seller ID

        firestore = FirebaseFirestore.getInstance()
        messagesCollection = firestore.collection("messages")

        getProduct(productId)

        // Set a click listener for the send button
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(productId, senderId, userId, messageText) // Pass the productId parameter
            }
        }
    }





    private fun getProduct(productId: String) {
            val productsCollection = firestore.collection("products")
            val productDocument = productsCollection.document(productId)

            productDocument.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        product = document.toObject(Product::class.java)!!

                        // Load chat messages using userId and senderId
                        loadChatMessages(productId, senderId, userId)
                    } else {
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    finish()
                }
        }

        private fun loadChatMessages(productId: String, senderId: String, userId: String) {
            val conversationDocument = firestore.collection("conversations")
                .document("$productId-$senderId-$userId") // Document ID

            val chatMessagesCollection = conversationDocument.collection("chatMessages")

            chatMessagesCollection
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (snapshot != null) {
                        chatMessages.clear()
                        for (document in snapshot.documents) {
                            val chatMessage = document.toObject(ChatMessage::class.java)
                            chatMessage?.let { message ->
                                chatMessages.add(message)
                            }
                        }
                        chatMessageAdapter.notifyDataSetChanged()

                        // Scroll to the latest message
                        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                    }
                }
        }

    private fun sendMessage(productId: String, senderId: String, userId: String, messageText: String) {
        val conversationDocumentId = "$productId-$senderId-$userId" // Unique ID for the chat bundle
        val conversationDocument = firestore.collection("conversations")
            .document(conversationDocumentId)

        val chatMessagesCollection = conversationDocument.collection("chatMessages")

        val chatMessage = ChatMessage(
            senderId = senderId,
            userId = userId,
            productId = productId,
            message = messageText,
            timestamp = Date().time
        )

        // Generate a unique ID for the chat message
        val chatMessageId = chatMessagesCollection.document().id

        chatMessagesCollection.document(chatMessageId).set(chatMessage)
            .addOnSuccessListener {
                // Send push notification to the seller
                sendPushNotificationToSeller(senderId, messageText)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

        // Update the chat bundle document with the latest message details
        val chatBundleData = mapOf(
            "lastMessage" to messageText,
            "lastMessageTime" to chatMessage.timestamp
        )
        conversationDocument.set(chatBundleData, SetOptions.merge())
    }

    private fun sendPushNotificationToSeller(sellerId: String, messageText: String) {
        val topic = "seller_$sellerId" // Construct the topic using the seller's ID
        val notificationTitle = "New Message"
        val notificationBody = messageText

        val remoteMessage = RemoteMessage.Builder("$sellerId@gcm.googleapis.com")
            .setMessageId(java.lang.String.valueOf(System.currentTimeMillis()))
            .addData("title", notificationTitle)
            .addData("body", notificationBody)
            .setTtl(3600) // Set time to live for the message (optional)
            .build()

        try {
            FirebaseMessaging.getInstance().send(remoteMessage)
            // Push notification sent successfully
            // Handle the successful completion
        } catch (exception: Exception) {
            // Error sending push notification
            // Handle the exception accordingly
        }
    }


    private fun showProductNameList(products: List<Product>) {
            productNameListLayout.removeAllViews()
            for (product in products) {
                val productNameTextView = LayoutInflater.from(this).inflate(
                    R.layout.item_product_name,
                    productNameListLayout,
                    false
                ) as TextView
                productNameTextView.text = product.name
                productNameTextView.setOnClickListener {
                    // Handle product name click event
                    // You can implement the logic here to load messages for the selected product
                }
                productNameListLayout.addView(productNameTextView)
            }
        }

        private fun showMessageView(show: Boolean) {
            messageViewLayout.visibility = if (show) View.VISIBLE else View.GONE
        }

        companion object {
            private const val TAG = "ChatActivity"
        }
    }

