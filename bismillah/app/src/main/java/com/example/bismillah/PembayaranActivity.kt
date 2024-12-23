package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*

class PembayaranActivity : AppCompatActivity() {

    private lateinit var rvPesanan: RecyclerView
    private lateinit var tvTotalHarga: TextView
    private lateinit var rgMetodePembayaran: RadioGroup
    private lateinit var btnKonfirmasi: Button
    private lateinit var adapterPesanan: AdapterPesanan
    private lateinit var tvPoinPengguna: TextView
    private lateinit var switchGunakanPoin: Switch

    private lateinit var database: DatabaseReference
    private var totalHargaSetelahDiskon: Int = 0
    private var poinPengguna: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        rvPesanan = findViewById(R.id.rvPesanan)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        rgMetodePembayaran = findViewById(R.id.rgMetodePembayaran)
        btnKonfirmasi = findViewById(R.id.btnKonfirmasiPembayaran)
        tvPoinPengguna = findViewById(R.id.tvPoinPengguna)
        switchGunakanPoin = findViewById(R.id.switchGunakanPoin)

        val totalHarga = intent.getIntExtra("TOTAL_HARGA", 0)
        val daftarPesanan: ArrayList<DataClass.Makanan>? =
            intent.getParcelableArrayListExtra<DataClass.Makanan>("DAFTAR_PESANAN")

        setupRecyclerView(daftarPesanan)
        fetchPoinPengguna()

        // Update harga total awal
        tvTotalHarga.text = "Total: Rp ${String.format("%,d", totalHarga).replace(",", ".")}"
        totalHargaSetelahDiskon = totalHarga

        // Listener untuk Switch Gunakan Poin
        switchGunakanPoin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Gunakan poin untuk diskon
                val diskon = poinPengguna
                if (diskon >= totalHarga) {
                    totalHargaSetelahDiskon = 0
                } else {
                    totalHargaSetelahDiskon = totalHarga - diskon
                }
            } else {
                // Kembalikan ke harga asli
                totalHargaSetelahDiskon = totalHarga
            }
            // Perbarui tampilan harga total
            tvTotalHarga.text = "Total: Rp ${String.format("%,d", totalHargaSetelahDiskon).replace(",", ".")}"
        }

        btnKonfirmasi.setOnClickListener {
            val poinTerpakai = if (switchGunakanPoin.isChecked) poinPengguna.coerceAtMost(totalHarga) else 0
            handlePaymentConfirmation(daftarPesanan, totalHargaSetelahDiskon, poinTerpakai)
        }
    }

    private fun setupRecyclerView(daftarPesanan: ArrayList<DataClass.Makanan>?) {
        if (!daftarPesanan.isNullOrEmpty()) {
            val pesananList = daftarPesanan.map {
                DataClass.Pesanan(
                    it.nama,
                    "Rp ${String.format("%,d", it.harga.replace(".", "").toInt()).replace(",", ".")}"
                )
            }
            rvPesanan.layoutManager = LinearLayoutManager(this)
            adapterPesanan = AdapterPesanan(pesananList)
            rvPesanan.adapter = adapterPesanan
        }
    }

    private fun fetchPoinPengguna() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("users").child(userId)

        database.child("poin").get().addOnSuccessListener { snapshot ->
            poinPengguna = snapshot.getValue(Int::class.java) ?: 0
            tvPoinPengguna.text = "Poin Anda: $poinPengguna"
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil data poin pengguna", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePaymentConfirmation(
        daftarPesanan: ArrayList<DataClass.Makanan>?,
        totalHarga: Int,
        poinTerpakai: Int
    ) {
        val metodePembayaran = when (rgMetodePembayaran.checkedRadioButtonId) {
            R.id.rbTransfer -> "Transfer Bank"
            R.id.rbEWallet -> "E-Wallet"
            R.id.rbCOD -> "COD"
            else -> null
        }

        if (metodePembayaran == null) {
            Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu!", Toast.LENGTH_SHORT)
                .show()
        } else {
            val namaProduk = daftarPesanan?.joinToString(", ") { it.nama } ?: intent.getStringExtra("NAMA_PRODUK") ?: "Produk Tidak Diketahui"

            // Tambahkan poin pengguna setelah transaksi (100 Rupiah = 1 poin)
            updatePoinPenggunaSetelahTransaksi(totalHarga / 100, poinTerpakai)

            // Arahkan ke QrPembayaranActivity
            val intent = Intent(this, QrPembayaranActivity::class.java)
            intent.putExtra("TOTAL_HARGA", totalHarga)
            intent.putExtra("METODE_PEMBAYARAN", metodePembayaran)
            intent.putExtra("NAMA_PRODUK", namaProduk)
            intent.putExtra("POIN_TERPAKAI", poinTerpakai)
            startActivity(intent)
            finish()
        }
    }

    private fun updatePoinPenggunaSetelahTransaksi(poinBaru: Int, poinTerpakai: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.child("poin").get().addOnSuccessListener { snapshot ->
            val poinSaatIni = snapshot.getValue(Int::class.java) ?: 0
            val totalPoin = poinSaatIni - poinTerpakai + poinBaru // Kurangi poin yang digunakan, tambahkan poin baru

            userRef.child("poin").setValue(totalPoin)
                .addOnSuccessListener {
                    Toast.makeText(this, "Poin berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui poin pengguna.", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil data poin pengguna.", Toast.LENGTH_SHORT).show()
        }
    }

}
