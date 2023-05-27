package com.example.marketplace.Main_Page

import com.example.marketplace.Products.ProductDetailsActivity
import com.example.marketplace.Adapter.InventoryItem
import com.example.marketplace.Adapter.InventoryAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.Chat.ChatActivity
import com.example.marketplace.R
import com.example.marketplace.Products.SellProductActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainPageActivity : AppCompatActivity() {

    private lateinit var inventoryListView: ListView
    private lateinit var sellButton: Button
    private lateinit var logoutButton: Button
    private lateinit var locationFilterSpinner: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button

    private lateinit var inventoryAdapter: InventoryAdapter
    private val inventoryList: MutableList<InventoryItem> = mutableListOf()

    private lateinit var firestore: FirebaseFirestore

    private var selectedLocation: String = "All" // Selected location variable
    private var senderId: String? = null
    private var userId: String? = null

    private lateinit var locationFilterAdapter: ArrayAdapter<String> // Location filter adapter

    private val SELL_PRODUCT_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)


        val chatButton: Button = findViewById(R.id.chatbuttn)
        chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }



        inventoryListView = findViewById(R.id.inventoryListView)
        sellButton = findViewById(R.id.sellButton)

        locationFilterSpinner = findViewById(R.id.locationFilterSpinner)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        logoutButton = findViewById(R.id.logoutbutn)

        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.email // Assign userId here

        // Set up the inventory adapter and attach it to the ListView
        inventoryAdapter = InventoryAdapter(this, inventoryList)
        inventoryListView.adapter = inventoryAdapter

        sellButton.setOnClickListener {
            val intent = Intent(this, SellProductActivity::class.java)
            startActivityForResult(intent, SELL_PRODUCT_REQUEST_CODE)
        }

        // Load the inventory from Firebase Firestore
        loadInventory()

        // Set up the location filter spinner
        val locationFilterOptions: MutableList<String> = mutableListOf()
        locationFilterAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, locationFilterOptions)
        locationFilterSpinner.adapter = locationFilterAdapter
        locationFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                // Check if position is within the bounds of the locationFilterOptions list
                if (position >= 0 && position < locationFilterOptions.size) {
                    // Update the selected location and reload the inventory
                    selectedLocation = locationFilterOptions[position]
                    loadInventory()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                searchInventory(searchQuery)
            } else {
                loadInventory()
            }
        }

        logoutButton.setOnClickListener {
            // Perform logout actions here
            logout()
        }

        // Set up the click listener for inventory items
        inventoryListView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = inventoryAdapter.getItem(position)
            selectedItem?.let { item ->
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("productInfo", item as Parcelable)

                val productId = item.productId
                if (productId != null) {
                    firestore.collection("products")
                        .document(productId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val senderId = documentSnapshot.getString("senderId")
                            intent.putExtra("senderId", senderId)
                            intent.putExtra("userId", userId)
                            intent.putExtra("productId", productId)

                            startActivity(intent)
                        }
                        .addOnFailureListener { exception ->
                            // Handle the failure to retrieve the senderId
                            Toast.makeText(this, "Failed to retrieve senderId.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "No productId found.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }






        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELL_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the activity or load the inventory again
            loadInventory()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        // Redirect the user to the login activity or any other desired screen
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the current activity to prevent the user from navigating back to it using the back button
    }

    private fun loadInventory() {
        val productsCollection = firestore.collection("products")

        productsCollection.get().addOnSuccessListener { querySnapshot ->
            val inventoryItems: MutableList<InventoryItem> = mutableListOf()
            val locationSet: MutableSet<String> = mutableSetOf()

            for (document in querySnapshot.documents) {
                val name = document.getString("name")
                val description = document.getString("description")
                val price = document.getDouble("price")
                val location = document.getString("location")
                val image = document.getString("image")

                if (name != null && description != null && price != null && location != null && image != null) {
                    val sellerId = document.getString("sellerId")
                    val userId = FirebaseAuth.getInstance().currentUser?.email
                    val inventoryItem = InventoryItem(name, description, price, location, image, sellerId, document.id, userId)
                    inventoryItems.add(inventoryItem)
                    locationSet.add(location)
                }

            }

            // Update the location filter spinner adapter
            val locationFilterOptions = mutableListOf("All")
            locationFilterOptions.addAll(locationSet)

            // Clear the existing adapter before setting the new one
            locationFilterAdapter.clear()
            locationFilterAdapter.addAll(locationFilterOptions)

            // Find the index of the selected location in the updated location filter options
            val selectedLocationIndex = locationFilterOptions.indexOf(selectedLocation)

            // Set the selected location on the location filter spinner
            locationFilterSpinner.setSelection(selectedLocationIndex)

            // Filter the inventory items based on the selected location
            val filteredItems = if (selectedLocation == "All") {
                inventoryItems
            } else {
                inventoryItems.filter { item ->
                    item.location == selectedLocation
                }
            }

            // Clear the existing list and add the new inventory items
            inventoryList.clear()
            inventoryList.addAll(filteredItems)

            // Notify the adapter that the data set has changed
            inventoryAdapter.notifyDataSetChanged()
        }
    }

    private fun searchInventory(query: String) {
        val productsCollection = firestore.collection("products")

        productsCollection.get().addOnSuccessListener { querySnapshot ->
            val inventoryItems: MutableList<InventoryItem> = mutableListOf()

            for (document in querySnapshot.documents) {
                val name = document.getString("name")
                val description = document.getString("description")
                val price = document.getDouble("price")
                val location = document.getString("location")
                val image = document.getString("image")

                if (name != null && description != null && price != null && location != null && image != null) {
                    val sellerId = document.getString("sellerId")
                    val inventoryItem = InventoryItem(name, description, price, location, image, sellerId, document.id, userId)

                    // Filter the inventory items based on the search query
                    if (name.contains(query, ignoreCase = true) || description.contains(
                            query,
                            ignoreCase = true
                        )
                    ) {
                        inventoryItems.add(inventoryItem)
                    }
                }
            }

            // Clear the existing list and add the new inventory items
            inventoryList.clear()
            inventoryList.addAll(inventoryItems)

            // Notify the adapter that the data set has changed
            inventoryAdapter.notifyDataSetChanged()
        }
    }
}
