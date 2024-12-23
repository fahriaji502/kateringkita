package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*

class QrPembayaranActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_pembayaran)

        val ivQRCode: ImageView = findViewById(R.id.ivQRCode)
        val btnSelesai: Button = findViewById(R.id.btnSelesaiPembayaran)

        // Ambil data dari Intent
        val totalHarga = intent.getIntExtra("TOTAL_HARGA", 0)
        val metodePembayaran = intent.getStringExtra("METODE_PEMBAYARAN") ?: "E-Wallet"
        val daftarPesanan = intent.getStringExtra("NAMA_PRODUK") ?: "Tidak Ada Produk"
        val poinTerpakai = intent.getIntExtra("POIN_TERPAKAI", 0)

        // Generate QR Code
        try {
            val qrData = """
                Pembayaran:
                $metodePembayaran
                Total: Rp ${String.format("%,d", totalHarga).replace(",", ".")}
                Produk: $daftarPesanan
                Poin Digunakan: $poinTerpakai
            """.trimIndent()

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400)
            ivQRCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal membuat QR Code!", Toast.LENGTH_SHORT).show()
        }

        // Tombol untuk simulasi pembayaran selesai
        btnSelesai.setOnClickListener {
            val idTransaksi = "TX" + System.currentTimeMillis()
            simpanTransaksiFirebase(
                idTransaksi,
                totalHarga,
                metodePembayaran,
                daftarPesanan,
                poinTerpakai
            )
        }
    }

    private fun simpanTransaksiFirebase(
        idTransaksi: String,
        totalHarga: Int,
        metodePembayaran: String,
        namaProduk: String,
        poinTerpakai: Int
    ) {
        val database = FirebaseDatabase.getInstance()
        val transaksiRef = database.getReference("orderHistory")
        val userRef = database.getReference("users")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val transaksiData = mapOf(
            "idTransaksi" to idTransaksi,
            "totalHarga" to totalHarga,
            "metodePembayaran" to metodePembayaran,
            "tanggalTransaksi" to SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date()),
            "namaProduk" to namaProduk,
            "poinTerpakai" to poinTerpakai
        )

        // Simpan transaksi di bawah node userId
        transaksiRef.child(userId).child(idTransaksi).setValue(transaksiData)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()

                // Perbarui poin pengguna
                userRef.child(userId).child("poin")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentPoin = snapshot.getValue(Int::class.java) ?: 0
                            val tambahanPoin = (totalHarga / 1000) // Tambahkan poin reward
                            val totalPoin =
                                (currentPoin - poinTerpakai + tambahanPoin).coerceAtLeast(0) // Kurangi poin terpakai

                            userRef.child(userId).child("poin").setValue(totalPoin)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@QrPembayaranActivity,
                                        "Poin berhasil diperbarui! Total poin: $totalPoin",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Arahkan ke PesananActivity setelah transaksi selesai
                                    val intent = Intent(
                                        this@QrPembayaranActivity,
                                        PesananActivity::class.java
                                    )
                                    intent.putExtra("ID_TRANSAKSI", idTransaksi)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@QrPembayaranActivity,
                                        "Gagal memperbarui poin pengguna!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@QrPembayaranActivity,
                                "Gagal mengambil data poin: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Gagal menyimpan transaksi: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

