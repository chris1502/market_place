package com.example.marketplace.Products

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.marketplace.Chat.ChatActivity
import com.example.marketplace.Main_Page.MainPageActivity
import com.example.marketplace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class SellProductActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var productImageView: ImageView
    private lateinit var uploadImageButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var sellButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 1

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_product)

        firestore = FirebaseFirestore.getInstance()
        storageReference =
            FirebaseStorage.getInstance("gs://market-place-45346.appspot.com/").reference

        productImageView = findViewById(R.id.itemImageView)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        priceEditText = findViewById(R.id.priceEditText)
        locationEditText = findViewById(R.id.locationEditText)
        sellButton = findViewById(R.id.sellButton)

        uploadImageButton.setOnClickListener {
            openImagePicker()
        }

        sellButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val price = priceEditText.text.toString().toDouble()
            val location = locationEditText.text.toString()

            // Retrieve the current user's email
            val sellerEmail = FirebaseAuth.getInstance().currentUser?.email

            if (imageUri != null && sellerEmail != null) {
                val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image uploaded successfully, get the download URL
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                val product = hashMapOf(
                                    "name" to name,
                                    "description" to description,
                                    "price" to price,
                                    "location" to location,
                                    "image" to uri.toString(),
                                    "senderId" to sellerEmail, // Set the seller's email as the senderId
                                    "deviceToken" to "", // Initialize the deviceToken property
                                )

                                // Add a new document with an automatically generated unique ID
                                firestore.collection("products")
                                    .add(product)
                                    .addOnSuccessListener { documentReference ->
                                        val productId = documentReference.id
                                        // Update the product document with the document ID
                                        firestore.collection("products")
                                            .document(productId)
                                            .update("productId", productId)
                                            .addOnSuccessListener {
                                                // Product data and ID stored successfully
                                                Toast.makeText(
                                                    this,
                                                    "Product listed successfully! ID: $productId",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                // After a user logs in or registers
                                                FirebaseMessaging.getInstance().token
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            val token = task.result

                                                            // Save the device token in a variable
                                                            val deviceToken = token

                                                            // Update the product with the device token
                                                            product["deviceToken"] = deviceToken

                                                            // Update the product document with the device token
                                                            firestore.collection("products")
                                                                .document(productId)
                                                                .update("deviceToken", deviceToken)
                                                                .addOnSuccessListener {
                                                                    // Device token saved successfully in the product document
                                                                }
                                                                .addOnFailureListener { exception ->
                                                                    // Error saving device token in the product document
                                                                    Log.e(
                                                                        "FCM_TOKEN",
                                                                        "Failed to save device token in the product document: $exception"
                                                                    )
                                                                }
                                                        } else {
                                                            Log.e(
                                                                "FCM_TOKEN",
                                                                "Failed to retrieve device token: ${task.exception}"
                                                            )
                                                        }
                                                    }

                                                // Navigate to MainPageActivity and pass the productId as an extra
                                                val intent =
                                                    Intent(this, MainPageActivity::class.java)
                                                intent.putExtra("productId", productId)
                                                startActivity(intent)
                                            }
                                            .addOnFailureListener {
                                                // Error updating product document with ID
                                                Toast.makeText(
                                                    this,
                                                    "Failed to list product.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        // Error storing product data
                                        Toast.makeText(
                                            this,
                                            "Failed to list product.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    }
            }
        }
    }

        private fun openImagePicker() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                productImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
