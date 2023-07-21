package com.example.friendlychattingapp.authenticate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.friendlychattingapp.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            startNextActivity()
        }, 2000L)
    }

    private fun startNextActivity() {
        val intent = Intent(this, LoginActivity::class.java) // Replace NextActivity with your desired activity
        startActivity(intent)
        finish() // Finish the splash screen activity so that the user cannot navigate back to it
    }
}