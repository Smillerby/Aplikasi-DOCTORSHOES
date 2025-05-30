package com.example.projectmp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView: VideoView = findViewById(R.id.videoView)
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.v1)

        videoView.setVideoURI(videoUri)
        videoView.setOnCompletionListener {
            startMainActivity()
        }

        videoView.start()
    }

    private fun startMainActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
}