package com.example.projectmp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_login)

        val loginUserButton = findViewById<Button>(R.id.loginUserButton)

        // Aksi ketika tombol Login Pengguna diklik
        loginUserButton.setOnClickListener {
            Log.d("SelectLoginActivity", "Login button clicked")  // Log untuk debugging
            val intent = Intent(this, LoginActivity::class.java) // Halaman login pengguna
            startActivity(intent)
        }
    }
}
