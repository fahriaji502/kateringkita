package com.example.bismillah

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UbahAlamatActivity : AppCompatActivity() {

    private lateinit var edtAlamat: EditText  // EditText untuk memasukkan alamat
    private lateinit var editDetailAlamat: EditText  // EditText untuk detail alamat
    private lateinit var btnSimpan: Button  // Tombol untuk menyimpan perubahan

    private lateinit var database: DatabaseReference  // Referensi Realtime Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_alamat)

        // Menginisialisasi elemen UI
        edtAlamat = findViewById(R.id.edtAlamat)
        editDetailAlamat = findViewById(R.id.editDetailAlamat)
        btnSimpan = findViewById(R.id.btnSimpan)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("users")

        // Mendapatkan ID pengguna yang sedang login
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi", Toast.LENGTH_SHORT).show()
            return
        }

        // Menangani klik tombol "Simpan"
        btnSimpan.setOnClickListener {
            val alamatBaru = edtAlamat.text.toString()
            val detailAlamatBaru = editDetailAlamat.text.toString()

            // Validasi alamat dan detail alamat
            if (alamatBaru.isNotEmpty() && detailAlamatBaru.isNotEmpty()) {
                // Simpan data ke Realtime Database di bawah ID pengguna yang sedang login
                val alamatData = mapOf(
                    "alamat" to alamatBaru,
                    "detail_alamat" to detailAlamatBaru  // Pastikan nama field sesuai dengan struktur data Anda
                )

                // Menyimpan alamat dan detail alamat di dalam node pengguna yang sesuai
                database.child(userId).updateChildren(alamatData)
                    .addOnSuccessListener {
                        // Tampilkan Toast sebagai konfirmasi
                        Toast.makeText(this, "Alamat berhasil disimpan", Toast.LENGTH_SHORT).show()
                        finish()  // Kembali ke activity sebelumnya setelah menyimpan
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menyimpan alamat", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Alamat dan detail alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
