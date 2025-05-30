package com.example.projectmp

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.*

class DatabaseHelper(private val context: Context) {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val notesRef: DatabaseReference = database.reference.child("notes")

    // Menyimpan catatan baru ke Firebase, dengan list sepatu (shoes)
    fun insertNote(
        ownerName: String,
        phoneNumber: String,
        shoes: List<ShoeItem>,
        orderDate: String,
        location: String,
        paymentMethod: String,
        kecamatan: String,
        desa: String,
        kodePos: String
    ) {
        val noteId = notesRef.push().key ?: return
        val newNote = Note(
            id = noteId,
            ownerName = ownerName,
            phoneNumber = phoneNumber,
            shoes = shoes,
            orderDate = orderDate,
            location = location,
            paymentMethod = paymentMethod,
            kecamatan = kecamatan,
            desa = desa,
            kodePos = kodePos,
            isProcessed = false
        )

        notesRef.child(noteId).setValue(newNote)
            .addOnSuccessListener {
                Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save note", Toast.LENGTH_SHORT).show()
            }
    }

    // Mendapatkan semua catatan dari Firebase
    fun getAllNotes(callback: (List<Note>) -> Unit) {
        notesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notesList = mutableListOf<Note>()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.let { notesList.add(it) }
                }
                callback(notesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load notes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Mengupdate catatan di Firebase
    fun updateNote(note: Note) {
        notesRef.child(note.id).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update note", Toast.LENGTH_SHORT).show()
            }
    }

    // Mengambil catatan berdasarkan ID dari Firebase
    fun getNoteByID(noteId: String, callback: (Note?) -> Unit) {
        notesRef.child(noteId).get().addOnSuccessListener {
            val note = it.getValue(Note::class.java)
            callback(note)
        }.addOnFailureListener {
            callback(null)
            Toast.makeText(context, "Failed to retrieve note", Toast.LENGTH_SHORT).show()
        }
    }

    // Menghapus catatan dari Firebase
    fun deleteNote(noteId: String) {
        notesRef.child(noteId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show()
            }
    }
}
