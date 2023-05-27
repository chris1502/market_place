package com.example.marketplace.User

import com.example.marketplace.Main_Page.MainPageActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInButton = findViewById<Button>(R.id.signInButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)

        signInButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show()
            }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
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

    companion object {
        private const val TAG = "LoginActivity"
    }
}
