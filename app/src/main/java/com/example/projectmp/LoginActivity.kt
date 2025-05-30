package com.example.projectmp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()
            loginUser(username, password)
        }

        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId ?: "")

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value.toString()
                        if (role == "customer") {
                            startActivity(Intent(this, MainActivity::class.java))
                        } else if (role == "employee") {
                            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
                        } else {
                            Toast.makeText(this, "Role tidak dikenali", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
