package com.example.marketplace.Main_Page

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.R
import com.example.marketplace.User.SignInActivity
import com.example.marketplace.User.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val signInButton = findViewById<Button>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }

        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}
