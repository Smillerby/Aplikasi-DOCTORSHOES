package com.example.projectmp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmp.databinding.ActivityUpdateNoteBinding
import com.google.firebase.database.FirebaseDatabase

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private val database = FirebaseDatabase.getInstance().getReference("notes")
    private var noteId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteId = intent.getStringExtra("note_id") ?: ""
        if (noteId.isEmpty()) {
            Toast.makeText(this, "Invalid note ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load note details
        database.child(noteId).get().addOnSuccessListener { snapshot ->
            val note = snapshot.getValue(Note::class.java)
            if (note != null) {
                binding.updateOwnerNameEditText.setText(note.ownerName)

                if (note.shoes.isNotEmpty()) {
                    binding.updateShoeNameEditText.setText(note.shoes[0].shoeName)
                } else {
                    binding.updateShoeNameEditText.setText("")
                }

                binding.updateOrderDateEditText.setText(note.orderDate)
                binding.updatePhoneNumberEditText.setText(note.phoneNumber)
                binding.updateLocationEditText.setText(note.location)

            } else {
                Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load note", Toast.LENGTH_SHORT).show()
            finish()
        }

// Save updated note
        binding.updateSaveButton.setOnClickListener {
            val shoeName = binding.updateShoeNameEditText.text.toString()

            val updatedShoes = listOf(ShoeItem(shoeName = shoeName, serviceType = ""))

            val updatedNote = Note(
                id = noteId,
                ownerName = binding.updateOwnerNameEditText.text.toString(),
                phoneNumber = binding.updatePhoneNumberEditText.text.toString(),
                shoes = updatedShoes,
                orderDate = binding.updateOrderDateEditText.text.toString(),
                location = binding.updateLocationEditText.text.toString(),
                paymentMethod = "",  // Isi sesuai input jika ada
                kecamatan = "",
                desa = "",
                kodePos = "",
                isProcessed = false
            )

            database.child(noteId).setValue(updatedNote)
                .addOnSuccessListener {
                    Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show()
                }
        }
    }
}