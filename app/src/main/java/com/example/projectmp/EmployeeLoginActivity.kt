package com.example.projectmp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmp.databinding.ActivityEmployeeLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EmployeeLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.employeeLoginButton.setOnClickListener {
            val username = binding.employeeLoginUsername.text.toString()
            val password = binding.employeeLoginPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginAsEmployee(username, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginAsEmployee(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val database = FirebaseDatabase.getInstance().getReference("Users").child(userId ?: "")

                    database.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value.toString()
                        if (role == "employee") {
                            Toast.makeText(this, "Employee Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, EmployeeDashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            auth.signOut()
                            Toast.makeText(this, "Unauthorized Access", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
