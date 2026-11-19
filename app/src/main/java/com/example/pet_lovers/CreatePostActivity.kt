package com.example.pet_lovers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreatePostActivity : AppCompatActivity() {

    lateinit var iId : String
    lateinit var iUsername : String
    lateinit var iEmail : String


    lateinit var photo: ImageView
    lateinit var caption: EditText
    lateinit var count : TextView


    private val cloudName = "roth1qua"
    private val uploadPreset = "pet_lovers"

    var imageUri : Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()){
            uri: Uri? ->
            if (uri != null){
                imageUri = uri
                photo = findViewById<ImageView>(R.id.photo)
                photo.setImageURI(uri)
            }
        }




        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_create_post)


    }
    fun selectPhoto(view: View) {
        galleryLauncher.launch("images/*")
    }
    fun createPost(view: View) {
        iId = intent.getStringExtra("Id").toString()
        iUsername = intent.getStringExtra("UserName").toString()
        iEmail = intent.getStringExtra("Email").toString()

        if (imageUri != null) {
            Toast.makeText(
                this,
                "Please select an images",
                Toast.LENGTH_SHORT
            ).show()

            return
        }
        if (caption.text.toString().length > 100) {
            Toast.makeText(
                this,
                "caption must be less then 100 characters",
                Toast.LENGTH_SHORT
            ).show()

            return

        }
        uploadImage()
    }

    private fun uploadImage() {
        TODO("Not yet implemented")
    }


}



