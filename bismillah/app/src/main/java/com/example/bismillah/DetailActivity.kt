package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class DetailActivity : AppCompatActivity() {

    private var jumlahPorsi = 1
    private var hargaPerItem = 150000 // Harga satuan produk, akan diambil dari Intent
    private lateinit var tvJumlahPorsi: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var tvPorsi: TextView
    private lateinit var tvHargaDetail: TextView
    private lateinit var tvNamaProduk: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnCheckout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Ambil data dari Intent
        val imageUrl = intent.getStringExtra("DETAIL_IMAGE") ?: "" // Ambil URL gambar dari Intent
        val title = intent.getStringExtra("DETAIL_TITLE") ?: "No Title"
        val description = intent.getStringExtra("DETAIL_DESCRIPTION") ?: "No Description"
        hargaPerItem = intent.getIntExtra("DETAIL_PRICE", 150000) // Ambil harga sebagai Int

        // Inisialisasi View
        val imageView: ImageView = findViewById(R.id.image)
        val titleView: TextView = findViewById(R.id.paket1)
        tvDescription = findViewById(R.id.detail) // Deskripsi produk
        val imageAdd: ImageView = findViewById(R.id.imageAdd1)
        val imageMinus: ImageView = findViewById(R.id.imageMinus1)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }

        tvJumlahPorsi = findViewById(R.id.tvJumlahPorsi)
        tvTotalHarga = findViewById(R.id.tvTotalPrice)
        tvPorsi = findViewById(R.id.tvPaket1)
        tvHargaDetail = findViewById(R.id.TotalPrice)
        tvNamaProduk = findViewById(R.id.paket1)
        btnCheckout = findViewById(R.id.btnCheckout)

        // Set data awal ke View
        titleView.text = title
        tvDescription.text = description
        tvHargaDetail.text = "Rp ${String.format("%,d", hargaPerItem).replace(",", ".")}"
        tvNamaProduk.text = title

        // Muat gambar menggunakan Glide
        Glide.with(this)
            .load(imageUrl) // URL gambar dari Intent
            .into(imageView)

        // Set listener untuk tombol "+" dan "-"
        imageAdd.setOnClickListener {
            jumlahPorsi++
            updateUI()
        }

        imageMinus.setOnClickListener {
            if (jumlahPorsi > 1) { // Menjaga agar jumlah porsi tidak kurang dari 1
                jumlahPorsi--
                updateUI()
            }
        }

        // Tombol Checkout
        btnCheckout.setOnClickListener {
            val intent = Intent(this, PembayaranActivity::class.java)
            intent.putExtra("NAMA_PRODUK", titleView.text.toString()) // Kirim Nama Produk
            intent.putExtra("TOTAL_HARGA", jumlahPorsi * hargaPerItem) // Kirim Total Harga
            startActivity(intent)
        }

        // Update UI awal
        updateUI()
    }

    // Fungsi untuk mengupdate UI (jumlah porsi dan total harga)
    private fun updateUI() {
        val totalHarga = jumlahPorsi * hargaPerItem
        tvPorsi.text = jumlahPorsi.toString()
        tvJumlahPorsi.text = "$jumlahPorsi minggu"
        tvTotalHarga.text = "Rp ${String.format("%,d", totalHarga).replace(",", ".")}"
    }
}

