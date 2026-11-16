
package com.example.pet_lovers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
    }

    fun registerUser(view: View) {
        startActivity(
            Intent(this, RegisterActivity::class.java)
        )
    }

    fun startFeed(view: View) {

        val sEmail = email.text.toString().trim()
        val sPassword = password.text.toString()

        if (sEmail.isEmpty() || sPassword.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(sEmail, sPassword)

            .addOnSuccessListener { result ->

                val uid = result.user?.uid
                val userEmail = result.user?.email

                if (uid == null) {
                    Toast.makeText(
                        this,
                        "User authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                // Get current user's data using UID
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .get()

                    .addOnSuccessListener { snapshot ->

                        if (snapshot.exists()) {

                            val userName =
                                snapshot.getValue(String::class.java)

                            val intent = Intent(
                                this,
                                FeedActivity::class.java
                            )

                            intent.putExtra("Id", uid)
                            intent.putExtra("email", userEmail)
                            intent.putExtra("username", userName)

                            startActivity(intent)
                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "User profile not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    .addOnFailureListener { error ->

                        Toast.makeText(
                            this,
                            "Database Error: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Login Failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
