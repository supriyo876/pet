package com.example.pet_lovers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


            caption = findViewById<EditText>(R.id.caption)
            count = findViewById<TextView>(R.id.count)

            caption.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    count.text = "${p0?.length}/100"
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })


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

    pprivate fun uploadImage() {

        val uri = imageUri ?: return

        Thread {

            try {

                val url = URL("https://api.cloudinary.com/v1_1/${cloudName}/image/upload")
                val connection = url.openConnection() as HttpURLConnection
                val boundary = "----AndroidBoundary"

                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

                val output = DataOutputStream(connection.outputStream)

                // Upload preset
                output.writeBytes("--$boundary\r\n")
                output.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
                output.writeBytes("$uploadPreset\r\n")

                // Image
                output.writeBytes("--$boundary\r\n")
                output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n")
                output.writeBytes("Content-Type: image/jpeg\r\n\r\n")

                contentResolver.openInputStream(uri)?.use { input ->

                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }

                output.writeBytes("\r\n")
                output.writeBytes("--$boundary--\r\n")
                output.flush()
                output.close()

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val response = connection.inputStream
                        .bufferedReader()
                        .readText()

                    val json = JSONObject(response)
                    val imageUrl = json.getString("secure_url")

                    Handler(Looper.getMainLooper()).post {
                        savePost(imageUrl)
                    }

                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@CreatePostActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@CreatePostActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }

        }.start()
    }

    private fun savePost(imageUrl: String) {

        val postId = FirebaseDatabase.getInstance().getReference("posts").push().key!!

        val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val post = mapOf("id" to iId, "username" to iUsername, "time" to time, "photo" to imageUrl, "caption" to caption.text.toString())

        FirebaseDatabase.getInstance()
            .getReference("posts")
            .child(postId)
            .setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this@CreatePostActivity, "Post created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this@CreatePostActivity, error.message, Toast.LENGTH_SHORT).show()
            }
    }


}



