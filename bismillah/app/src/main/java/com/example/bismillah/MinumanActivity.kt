package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MinumanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MakananAdapter
    private lateinit var database: DatabaseReference
    private lateinit var tvJumlahPorsi: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var btnCheckout: Button
    private lateinit var imgPlace: ImageView

    private val minumanList = mutableListOf<DataClass.Makanan>()
    private val selectedItems = mutableListOf<DataClass.Makanan>()
    private var totalHarga = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minuman)

        // Inisialisasi Views
        tvJumlahPorsi = findViewById(R.id.tvJumlahPorsi)
        tvTotalHarga = findViewById(R.id.tvTotalPrice)
        btnCheckout = findViewById(R.id.btnCheckout)
        imgPlace = findViewById(R.id.imgPlace)

        val backButton: ImageView = findViewById(R.id.back)

        // Tombol back
        backButton.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        // Navigasi ke halaman alamat
        imgPlace.setOnClickListener {
            val intent = Intent(this, AlamatActivity::class.java)
            startActivity(intent)
        }

        // RecyclerView Setup
        recyclerView = findViewById(R.id.rvTrending)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter Setup dengan Callback
        adapter = MakananAdapter(minumanList, { totalItems, totalHargaBaru, selected ->
            updateTotal(totalItems, totalHargaBaru)
            selectedItems.clear()
            selectedItems.addAll(selected)
        }) { minuman ->
            Log.d("MinumanActivity", "Mengirim ke DetailActivity: Nama = ${minuman.nama}, Harga = ${minuman.harga}, Gambar = ${minuman.gambarUrl}, Deskripsi = ${minuman.description}")

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("DETAIL_IMAGE", minuman.gambarUrl) // Kirim URL Gambar
            intent.putExtra("DETAIL_TITLE", minuman.nama) // Kirim Nama Produk
            intent.putExtra("DETAIL_DESCRIPTION", minuman.description) // Kirim deskripsi produk dari Firebase
            intent.putExtra("DETAIL_PRICE", minuman.harga.toInt()) // Kirim Harga
            startActivity(intent)
        }


        recyclerView.adapter = adapter

        // Tombol Checkout
        btnCheckout.setOnClickListener {
            val intent = Intent(this, PembayaranActivity::class.java)
            intent.putExtra("TOTAL_HARGA", totalHarga) // Kirim total harga
            intent.putParcelableArrayListExtra("DAFTAR_PESANAN", ArrayList(selectedItems)) // Kirim daftar pesanan
            startActivity(intent)
        }

        // Fetch Data dari Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("products")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                minumanList.clear()
                for (productSnapshot in snapshot.children) {
                    val category = productSnapshot.child("category").getValue(String::class.java) ?: ""
                    if (category == "minuman") { // Filter hanya kategori minuman
                        val nama = productSnapshot.child("name").getValue(String::class.java) ?: ""
                        val harga = productSnapshot.child("price").getValue(Int::class.java) ?: 0
                        val gambarUrl = productSnapshot.child("gambarRes").getValue(String::class.java) ?: ""
                        val description = productSnapshot.child("description").getValue(String::class.java) ?: "Deskripsi tidak tersedia"

                        Log.d("Firebase", "Nama: $nama, Harga: Rp $harga, Kategori: $category")

                        minumanList.add(
                            DataClass.Makanan(
                                nama = nama,
                                harga = harga.toString(),
                                gambarUrl = gambarUrl,
                                description = description // Tambahkan deskripsi untuk digunakan di DetailActivity
                            )
                        )
                    }
                }
                adapter.notifyDataSetChanged() // Perbarui RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun updateTotal(totalItems: Int, totalHargaBaru: Int) {
        tvJumlahPorsi.text = "$totalItems minggu"
        totalHarga = totalHargaBaru
        tvTotalHarga.text = "Rp ${String.format("%,d", totalHarga).replace(",", ".")}"
    }
}


