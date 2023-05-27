package com.example.marketplace.Products

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.Adapter.InventoryItem
import com.example.marketplace.Chat.ChatActivity
import com.example.marketplace.R
import com.example.marketplace.databinding.ActivityProductDetailsBinding
import com.squareup.picasso.Picasso

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var inventoryItem: InventoryItem // Declare inventoryItem as a member property
    private var userId: String? = ""  // User ID variable
    private var sellerId: String? = ""
    private var productId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inventoryItem = intent.getParcelableExtra("productInfo") ?: throw IllegalArgumentException("Inventory item not found")
        val intent = intent
        userId = intent.getStringExtra("userId") // Retrieve the user ID from the intent extras
        sellerId = intent.getStringExtra("senderId")
        productId = intent.getStringExtra("productId")




        // Set the inventory item details to the respective views
        Picasso.get()
            .load(inventoryItem.image)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.itemImageView)

        binding.itemNameTextView.text = inventoryItem.name
        binding.itemDescriptionTextView.text = inventoryItem.description
        binding.itemPriceTextView.text = getString(R.string.price_placeholder, inventoryItem.price)

        binding.chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("senderId", sellerId)
            intent.putExtra("userId", userId)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
    }
}
