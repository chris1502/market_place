package com.example.marketplace.User

import com.example.marketplace.Main_Page.MainPageActivity
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signUpButton = findViewById<Button>(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign up success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            updateUserToDatabase(user)
                            updateUI(user)
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                this,
                                "Authentication failed. Something went wrong.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(null)
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in, redirect to the com.example.marketplace.com.example.marketplace.Main_Page.com.example.marketplace.Main_Page.com.example.marketplace.Main_Page.MainPageActivity
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUserToDatabase(user: FirebaseUser?) {
        user?.let {
            val userId = user.uid
            val email = user.email

            // Write the user data to the database
            database.child("users").child(userId).setValue(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User data written to the database.")
                    } else {
                        Log.w(TAG, "Failed to write user data to the database.", task.exception)
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, redirect to the com.example.marketplace.com.example.marketplace.Main_Page.com.example.marketplace.Main_Page.com.example.marketplace.Main_Page.MainPageActivity
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish the current activity to prevent going back to the sign-in screen
        }
    }
}