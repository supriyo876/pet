
package com.example.pet_lovers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        user = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirm = findViewById(R.id.ConfirmPassword)
    }

    fun startFeed(view: View) {

        val sUser = user.text.toString().trim()
        val sEmail = email.text.toString().trim()
        val sPassword = password.text.toString()
        val sConfirm = confirm.text.toString()

        // Check empty fields
        if (
            sUser.isEmpty() ||
            sEmail.isEmpty() ||
            sPassword.isEmpty() ||
            sConfirm.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Please fill all fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check password length
        if (sPassword.length < 6) {
            Toast.makeText(
                this,
                "Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check password confirmation
        if (sPassword != sConfirm) {
            Toast.makeText(
                this,
                "Passwords do not match",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create Firebase Authentication account
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(sEmail, sPassword)

            .addOnSuccessListener { result ->

                val uid = result.user?.uid

                if (uid == null) {
                    Toast.makeText(
                        this,
                        "Account creation failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                // Save username under the user's UID
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .setValue(sUser)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Registration successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Go to LoginActivity
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )

                        startActivity(intent)
                        finish()
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
                    "Registration Failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun loginPage(view: View) {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }
}
