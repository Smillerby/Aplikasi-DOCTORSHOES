package com.example.projectmp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EmployeeDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var noteList: MutableList<Note>
    private lateinit var notedAdapter: NotedAdapter
    private lateinit var notesRef: DatabaseReference
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var welcomeText: TextView

    private val CHANNEL_ID = "pesanan_baru_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_dashboard)

        initViews()
        initFirebase()
        setupDrawer()
        setupRecyclerView()
        setupNoteListener()
        createNotificationChannel()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        welcomeText = findViewById(R.id.employeeWelcomeText)

        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("email").value.toString()
            welcomeText.text = "Welcome, $username!"
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        noteList = mutableListOf()
        notedAdapter = NotedAdapter(noteList, this, false)
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = notedAdapter
    }

    private fun setupNoteListener() {
        notesRef = FirebaseDatabase.getInstance().getReference("notes")
        notesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val note = snapshot.getValue(Note::class.java)
                if (note != null && !note.isProcessed) {
                    noteList.add(note)
                    notedAdapter.refreshData(noteList)
                    sendLocalNotification(note.ownerName)
                    notesRef.child(note.id).child("isProcessed").setValue(true)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EmployeeDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pesanan Baru"
            val descriptionText = "Channel untuk notifikasi pesanan baru"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendLocalNotification(ownerName: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.dc1)
                .setContentTitle("Pesanan Baru")
                .setContentText("Pesanan baru dari $ownerName telah diterima.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
