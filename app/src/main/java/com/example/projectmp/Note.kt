package com.example.projectmp

data class ShoeItem(
    val shoeName: String = "",
    val serviceType: String = ""
)

data class Note(
    val id: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val shoes: List<ShoeItem> = listOf(),  // ⬅️ multi-sepatu
    val orderDate: String = "",
    val location: String = "",
    val paymentMethod: String = "",
    val kecamatan: String = "",
    val desa: String = "",
    val kodePos: String = "",
    val pickupMethod: String = "",
    var status: String = "Dijemput",
    val totalPrice: Int = 0, // Tambahkan ini jika belum
    val isProcessed: Boolean = false
)

