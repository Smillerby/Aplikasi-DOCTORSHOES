package com.example.projectmp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmp.databinding.ActivityAddNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private var selectedServiceType: String? = null
    private var selectedPaymentMethod: String? = null
    private var selectedPickupMethod: String? = null
    private var selectedKecamatan: String? = null
    private var selectedDesa: String? = null
    private var selectedKodePos: String? = null

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Harga per layanan
    private val hargaLayanan = mapOf(
        "Cuci Biasa" to 35000,
        "Cuci Premium" to 50000,
        "Repair" to 75000,
        "Whitening" to 35000
    )

    // Data lokasi
    private val dataKecamatanDesa = mapOf(
        "Babakancikao" to listOf("babakancikao", "cicadas", "cigelam", "cilangkap", "ciwareng", "hegarmanah", "kadumekar", "maracang", "mulyamekar"),
        "Bojong" to listOf("bojong barat", "bojong timur", "cibingbin", "cihanjawar", "cikeris", "cileunca", "cipeundeuy", "kertasari", "pangkalan", "pasanggrahan", "pawenang", "sindangpanon", "sindangsari", "sukamanah"),
        "bungursari" to listOf("bungursari", "cibening", "cibodas", "cibungur", "cikopo", "cinangka", "ciwangi", "dangdeur", "karangmukti", "wanakerta"),
        "campaka" to listOf("benteng", "campaka", "cipaksari", "cijaya", "cijunti", "cukumpay", "Cimahi", "crende", "cisaat", "keramukti"),
        "cibatu" to listOf("cibatu", "cibungkamanah", "cikadu", "cilandak", "cipancur", "ciparungsari", "cipinang", "cirangkong", "karyamekar", "wanawali"),
        "darangdan" to listOf("cilingga", "darangdan", "depok", "gununghejo", "legoksari", "linggamukti", "linggasari", "Mekarsari", "nagrak", "nangewer", "pasirangin", "sadarkarya", "sawit", "sirnamanah"),
        "jatiluhur" to listOf("bunder", "cibinong", "cikaobandung", "cilegong", "cisalada", "jatiluhur", "jatimekar", "kembangkuning", "mekargalih", "parakanlima"),
        "kiarapede" to listOf("cibeber", "ciracas", "gardu", "karangpedes", "margaluyu", "mekarjaya", "parakangarokgek", "pusakamulya", "sumbersasari", "taringgullandeuh"),
        "maniis" to listOf("cijati", "ciramahilir", "citamiang", "gunungkarung", "pasirjambu", "sinargalih", "sukamukti", "tegaldahar"),
        "pasawahan" to listOf("cidahu", "Ciherang", "cihuni", "kertajaya", "lebakanyar", "margasari", "pasawahan", "pasawahananyar", "pasawahankidul", "sawahkulon", "delaawi", "warungkadu"),
        "Purwakarta" to listOf("cipaisan", "ciseureuh", "citalang", "munjuljaya", "nagri kaler", "nagri kidul", "nagri tengah", "purwamekar", "sindangkasih", "tegalmunjul")
    )

    private val dataKodePos = mapOf(
        "Babakancikao" to "41151", "Bojong" to "41164", "bungursari" to "41181",
        "campaka" to "41180", "cibatu" to "41182", "darangdan" to "41163",
        "jatiluhur" to "41152", "kiarapede" to "41175", "maniis" to "41166",
        "pasawahan" to "41172", "Purwakarta" to "41113"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        setupSpinner(binding.paymentMethodSpinner, R.array.payment_methods) {
            selectedPaymentMethod = it
        }

        setupDateTimePicker()
        setupKecamatanSpinner()
        setupPickupMethodSpinner()
        setupAddShoeButton()
        updateTotalPrice() // initialize total price 0

        binding.saveButton.setOnClickListener { saveNote() }
    }

    private fun setupDateTimePicker() {
        binding.orderDateTimeEditText.isFocusable = false
        binding.orderDateTimeEditText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                TimePickerDialog(this, { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.orderDateTimeEditText.setText(dateFormat.format(calendar.time))
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter.createFromResource(this, arrayResId, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                onItemSelected(parent?.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupKecamatanSpinner() {
        val kecamatanList = dataKecamatanDesa.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kecamatanList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.kecamatanSpinner.adapter = adapter

        binding.kecamatanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedKecamatan = kecamatanList[position]
                updateDesaSpinner(selectedKecamatan!!)
                updateKodePosSpinner(selectedKecamatan!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateDesaSpinner(kecamatan: String) {
        val desaList = dataKecamatanDesa[kecamatan] ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, desaList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.desaSpinner.adapter = adapter
        binding.desaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedDesa = desaList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateKodePosSpinner(kecamatan: String) {
        val kodePos = dataKodePos[kecamatan] ?: ""
        val kodePosList = if (kodePos.isNotEmpty()) listOf(kodePos) else emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kodePosList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.kodePosSpinner.adapter = adapter
        binding.kodePosSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedKodePos = kodePosList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPickupMethodSpinner() {
        val pickupOptions = listOf("Diantar", "Dijemput")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pickupOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.pickupMethodSpinner.adapter = adapter
        binding.pickupMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedPickupMethod = pickupOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupAddShoeButton() {
        binding.addShoeButton.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.shoe_entry, null)
            val spinner = view.findViewById<Spinner>(R.id.serviceTypeSpinner)
            val options = hargaLayanan.keys.toList()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            // Set listener supaya tiap perubahan layanan update total harga
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    updateTotalPrice()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Tambah view shoe entry ke container
            binding.shoeContainer.addView(view)

            updateTotalPrice()
        }
    }

    private fun updateTotalPrice() {
        var total = 0
        for (i in 0 until binding.shoeContainer.childCount) {
            val entry = binding.shoeContainer.getChildAt(i)
            val spinner = entry.findViewById<Spinner>(R.id.serviceTypeSpinner)
            val selectedService = spinner.selectedItem?.toString() ?: ""
            total += hargaLayanan[selectedService] ?: 0
        }
        binding.totalHargaTextView.text = "Total Harga: Rp ${String.format("%,d", total)}"
    }

    private fun saveNote() {
        val ownerId = auth.currentUser?.uid ?: return
        val ownerName = binding.customerNameEditText.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val orderDateTime = binding.orderDateTimeEditText.text.toString()
        val location = binding.locationEditText.text.toString()

        if (ownerName.isBlank() || phoneNumber.isBlank() || orderDateTime.isBlank() || location.isBlank() ||
            selectedPaymentMethod == null || selectedPickupMethod == null || selectedKecamatan == null || selectedDesa == null || selectedKodePos == null
        ) {
            Toast.makeText(this, "Lengkapi semua data terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.shoeContainer.childCount == 0) {
            Toast.makeText(this, "Minimal tambahkan satu sepatu.", Toast.LENGTH_SHORT).show()
            return
        }

        val shoes = mutableListOf<Map<String, String>>()
        for (i in 0 until binding.shoeContainer.childCount) {
            val entry = binding.shoeContainer.getChildAt(i)
            val name = entry.findViewById<EditText>(R.id.shoeNameEditText).text.toString()
            val type = entry.findViewById<Spinner>(R.id.serviceTypeSpinner).selectedItem.toString()
            if (name.isBlank() || type.isBlank()) {
                Toast.makeText(this, "Isi semua data sepatu.", Toast.LENGTH_SHORT).show()
                return
            }
            shoes.add(mapOf("shoeName" to name, "serviceType" to type))
        }

        // Hitung total harga
        var total = 0
        for (i in 0 until binding.shoeContainer.childCount) {
            val entry = binding.shoeContainer.getChildAt(i)
            val spinner = entry.findViewById<Spinner>(R.id.serviceTypeSpinner)
            val selectedService = spinner.selectedItem?.toString() ?: ""
            total += hargaLayanan[selectedService] ?: 0
        }

        val noteId = database.reference.child("notes").push().key ?: return

        val noteData = mapOf(
            "id" to noteId,
            "ownerId" to ownerId,
            "ownerName" to ownerName,
            "phoneNumber" to phoneNumber,
            "orderDate" to orderDateTime,
            "location" to "$location, Desa: $selectedDesa, Kec: $selectedKecamatan, Kode Pos: $selectedKodePos",
            "paymentMethod" to selectedPaymentMethod,
            "pickupMethod" to selectedPickupMethod,
            "shoes" to shoes,
            "totalPrice" to total
        )

        database.reference.child("notes").child(noteId).setValue(noteData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }
}
