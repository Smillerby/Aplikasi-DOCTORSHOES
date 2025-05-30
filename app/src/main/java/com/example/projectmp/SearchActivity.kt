package com.example.projectmp

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {
    private lateinit var noteAdapter: NotedAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesList: MutableList<Note> // Menyimpan semua data catatan yang diterima dari Firebase
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        notesList = mutableListOf()
        noteAdapter = NotedAdapter(notesList, this, true)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = noteAdapter
        }

        database = FirebaseDatabase.getInstance().reference.child("notes")

        // Mengambil semua catatan dari Firebase
        getAllNotes()

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchNotes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchNotes(it)
                }
                return true
            }
        })
    }

    private fun getAllNotes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.let { notesList.add(it) }
                }
                noteAdapter.refreshData(notesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "Failed to load notes", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun searchNotes(query: String) {
        val filteredNotes = notesList.filter { note ->
            note.ownerName.contains(query, ignoreCase = true) ||
                    note.shoes.any {
                        it.shoeName.contains(
                            query,
                            ignoreCase = true
                        )
                    } ||  // Cek di list shoes
                    note.location.contains(query, ignoreCase = true) ||
                    note.phoneNumber.contains(query, ignoreCase = true) ||
                    note.orderDate.contains(query, ignoreCase = true)
        }
        noteAdapter.refreshData(filteredNotes)
    }
}