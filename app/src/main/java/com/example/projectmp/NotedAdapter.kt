package com.example.projectmp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class NotedAdapter(
    private var notes: List<Note>,
    private val context: Context,
    private val isCustomer: Boolean
) : RecyclerView.Adapter<NotedAdapter.NoteViewHolder>() {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("notes")

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ownerNameTextView: TextView = itemView.findViewById(R.id.ownerNameTextView)
        val shoeNameTextView: TextView = itemView.findViewById(R.id.shoeNameTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val shareButton: ImageView = itemView.findViewById(R.id.shareButton)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val pickupMethodTextView: TextView = itemView.findViewById(R.id.pickupMethodTextView)
        val orderDateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
        val updateStatusButton: Button = itemView.findViewById(R.id.updateStatusButton)
        val totalHargaTextView: TextView = itemView.findViewById(R.id.totalHargaTextView)

        val stepViews: List<TextView> = listOf(
            itemView.findViewById(R.id.step1),
            itemView.findViewById(R.id.step2),
            itemView.findViewById(R.id.step3),
            itemView.findViewById(R.id.step4),
            itemView.findViewById(R.id.step5)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.apply {
            ownerNameTextView.text = note.ownerName
            shoeNameTextView.text = note.shoes.joinToString(", ") { it.shoeName }
            phoneNumberTextView.text = note.phoneNumber
            locationTextView.text = note.location
            paymentMethodTextView.text = note.paymentMethod
            pickupMethodTextView.text = note.pickupMethod
            orderDateTextView.text = note.orderDate

            // Format total price with thousand separator
            val totalPrice = note.totalPrice // Menggunakan properti harga dari Note
            val formattedPrice = NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)
            totalHargaTextView.text = "Total: Rp $formattedPrice"

            updateStatusButton.visibility = if (isCustomer) View.GONE else View.VISIBLE
            updateButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
            shareButton.visibility = if (isCustomer) View.VISIBLE else View.GONE

            setupStatusProgress(note.status, stepViews)

            if (isCustomer) {
                setupShareButton(note, shareButton)
            } else {
                setupUpdateStatusButton(note, position, updateStatusButton)
            }
        }
    }

    private fun setupStatusProgress(status: String, stepViews: List<TextView>) {
        val statusIndex = when (status) {
            "Dijemput" -> 0
            "Diterima" -> 1
            "Proses" -> 2
            "Diantar" -> 3
            "Selesai" -> 4
            else -> -1
        }

        // Reset all steps color first
        stepViews.forEach { it.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray)) }

        if (statusIndex >= 0) {
            for (i in 0..statusIndex) {
                stepViews[i].setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
        }
    }

    private fun setupShareButton(note: Note, shareButton: ImageView) {
        shareButton.setOnClickListener {
            val shoesDetail = note.shoes.joinToString("\n") { "- ${it.shoeName} (${it.serviceType})" }
            val textToShare = """
                Owner: ${note.ownerName}
                Shoes:
                $shoesDetail
                Phone: ${note.phoneNumber}
                Location: ${note.location}
                Order Date: ${note.orderDate}
                Total: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(note.totalPrice)}
            """.trimIndent()

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
        }
    }

    private fun setupUpdateStatusButton(note: Note, position: Int, updateStatusButton: Button) {
        updateStatusButton.setOnClickListener {
            val nextStatus = when (note.status) {
                "Dijemput" -> "Diterima"
                "Diterima" -> "Proses"
                "Proses" -> "Diantar"
                "Diantar" -> "Selesai"
                else -> "Selesai"
            }

            dbRef.child(note.id).child("status").setValue(nextStatus)
                .addOnSuccessListener {
                    Toast.makeText(context, "Status diubah ke $nextStatus", Toast.LENGTH_SHORT).show()
                    note.status = nextStatus
                    notifyItemChanged(position)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal mengubah status", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun refreshData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    private fun deleteNoteFromFirebase(noteId: String) {
        dbRef.child(noteId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                refreshData(notes.filter { it.id != noteId })
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show()
            }
    }
}
