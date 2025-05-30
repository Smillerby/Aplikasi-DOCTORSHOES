package com.example.projectmp

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        videoView = findViewById(R.id.videoViewExample)

        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.seting)
        videoView.setVideoURI(videoUri)

        videoView.start()

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }
}
