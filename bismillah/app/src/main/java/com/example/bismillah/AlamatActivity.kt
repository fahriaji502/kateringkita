package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AlamatActivity : AppCompatActivity() {

    private lateinit var txtAlamat: TextView  // TextView untuk menampilkan alamat
    private lateinit var txtDetail: TextView  // TextView untuk menampilkan detail alamat
    private lateinit var database: DatabaseReference  // Firebase Realtime Database reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alamat)

        // Menginisialisasi elemen UI
        txtAlamat = findViewById(R.id.txtAlamat)
        txtDetail = findViewById(R.id.txtDetail)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }


        // Mendapatkan ID pengguna yang sedang login
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Pengguna tidak terautentikasi", Toast.LENGTH_SHORT).show()
            return
        }

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("users")

        // Mengambil data alamat dan detail alamat berdasarkan ID pengguna yang sedang login
        database.child(userId)  // Menggunakan ID pengguna yang sedang login
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val alamat = snapshot.child("alamat").getValue(String::class.java) ?: "Alamat tidak ditemukan"
                        val detailAlamat = snapshot.child("detail_alamat").getValue(String::class.java) ?: "Detail tempat tidak ditemukan"
                        txtAlamat.text = alamat  // Menampilkan alamat
                        txtDetail.text = detailAlamat  // Menampilkan detail tempat
                    } else {
                        txtAlamat.text = "Alamat tidak ditemukan"
                        txtDetail.text = "Detail tempat tidak ditemukan"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    txtAlamat.text = "Gagal mengambil alamat"
                    txtDetail.text = "Gagal mengambil detail tempat"
                }
            })

        // Menambahkan OnClickListener untuk tombol "Ubah"
        val btnUbah = findViewById<Button>(R.id.btnSimpan)
        btnUbah.setOnClickListener {
            // Pindah ke UbahAlamatActivity
            val intent = Intent(this, UbahAlamatActivity::class.java)
            startActivity(intent)
        }
    }
}
