package com.example.pet_lovers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.checkerframework.checker.units.qual.C

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_feed)

    }

    fun startCreatePost(view: View) {
        startActivity(
            Intent(this, CreatePostActivity::class.java)
        )
    }
}