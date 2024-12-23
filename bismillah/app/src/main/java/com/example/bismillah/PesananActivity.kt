package com.example.bismillah

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PesananActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvJml: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvDate: TextView
    private lateinit var btnHubungiWA: com.google.android.material.button.MaterialButton

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)

        // Inisialisasi View
        imgLogo = findViewById(R.id.imgLogo)
        tvNama = findViewById(R.id.tvNama)
        tvPrice = findViewById(R.id.tvPrice)
        tvJml = findViewById(R.id.tvJml)
        tvStatus = findViewById(R.id.tvStatus)
        tvDate = findViewById(R.id.tvDate)
        btnHubungiWA = findViewById(R.id.btnLogin)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }

        // Ambil ID Transaksi dari Intent
        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI")
        if (idTransaksi.isNullOrEmpty()) {
            Toast.makeText(this, "ID Transaksi tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ambil User ID dari FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Pengguna tidak terautentikasi!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().getReference("orderHistory")

        // Ambil data dari Firebase berdasarkan userId dan idTransaksi
        fetchPesananData(userId, idTransaksi)

        // Tombol Hubungi WA
        btnHubungiWA.setOnClickListener {
            val nomorWA = "6282269104266" // Ganti dengan nomor WhatsApp
            val pesan = "Halo, saya ingin menanyakan tentang pesanan saya dengan ID: $idTransaksi"

            val uri = Uri.parse("https://wa.me/$nomorWA?text=${Uri.encode(pesan)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Periksa apakah ada aplikasi WhatsApp yang dapat menangani intent
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp tidak terpasang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPesananData(userId: String, idTransaksi: String) {
        database.child(userId).child(idTransaksi).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val namaProduk = snapshot.child("namaProduk").value.toString()
                    val harga = snapshot.child("totalHarga").value.toString()
                    val metodePembayaran = snapshot.child("metodePembayaran").value.toString()
                    val tanggalTransaksi = snapshot.child("tanggalTransaksi").value.toString()

                    // Tampilkan data di UI
                    imgLogo.setImageResource(R.drawable.ic_history) // Logo default
                    tvNama.text = namaProduk
                    tvPrice.text = "Rp ${String.format("%,d", harga.toInt()).replace(",", ".")}"
                    tvJml.text = "Jumlah: " // Anda bisa menyesuaikan ini jika data jumlah tersedia
                    tvStatus.text = "Metode: $metodePembayaran"
                    tvDate.text = tanggalTransaksi
                } else {
                    Toast.makeText(this@PesananActivity, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PesananActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
