package com.example.pet_lovers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    lateinit var user: EditText

    lateinit var email: EditText

    lateinit var password: EditText

    lateinit var confirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        user = findViewById<EditText>(R.id.name)

        email = findViewById<EditText>(R.id.email)

        password = findViewById<EditText>(R.id.password)

        confirm = findViewById<EditText>(R.id.ConfirmPassword)
    }

    fun startFeed(view: View) {
        val sUser = user.text.toString()
        val sEmail = email.text.toString()
        val sPassword = password.text.toString()
        val sConfirm = confirm.text.toString()

        if (sPassword.length < 6){
            Toast.makeText(this@RegisterActivity,
                "pass must be 6 characters",
                Toast.LENGTH_SHORT). show()
            return
        }
        if (sPassword != sConfirm){
            Toast.makeText( this@RegisterActivity,
                "new pass and confirm pass doesn't match",
                Toast.LENGTH_SHORT).show()
        }

        val addListener =
            FirebaseDatabase.getInstance().getReference("users").orderByValue().equalTo(sUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot.exists()){
                            Toast.makeText( this@RegisterActivity,
                                "password already exits",
                                Toast.LENGTH_SHORT).show()
                            return
                        }
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail,sPassword)
                            .addOnSuccessListener { result ->
                                val uid = result.user!!.uid
                                FirebaseDatabase.getInstance()
                                    .getReference("users").child(uid)
                                    .setValue(sUser)

                                startActivity(Intent(this@RegisterActivity, FeedActivity::class.java))

                            }
                            .addOnFailureListener {
                                Toast.makeText( this@RegisterActivity,
                                    it.message,
                                    Toast.LENGTH_SHORT).show()
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText( this@RegisterActivity,
                            error.message,
                            Toast.LENGTH_SHORT).show()
                    }
                })


    }
    fun loginPage(view: View) {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
    }
}