package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class TrendingActivity : AppCompatActivity() {

    private lateinit var rvTrending: RecyclerView
    private lateinit var trendingAdapter: AdapterTrending
    private lateinit var database: DatabaseReference
    private val trendingList = ArrayList<DataClass>() // List data Trending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trending)

        // Inisialisasi RecyclerView
        rvTrending = findViewById(R.id.rvTrending)
        rvTrending.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Adapter
        trendingAdapter = AdapterTrending(trendingList)
        rvTrending.adapter = trendingAdapter

        // Listener klik untuk menuju ke DetailActivity
        trendingAdapter.onItemClick = { selectedItem ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("DETAIL_IMAGE", selectedItem.dataImage)
            intent.putExtra("DETAIL_TITLE", selectedItem.dataTitle)
            intent.putExtra("DETAIL_PRICE", selectedItem.dataPrice.toInt()) // Kirim harga sebagai Int
            intent.putExtra("DETAIL_DESCRIPTION", selectedItem.dataDesc)
            startActivity(intent)
        }

        // Fetch data Trending dari Firebase
        loadTrendingData()
    }

    private fun loadTrendingData() {
        database = FirebaseDatabase.getInstance().getReference("products")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trendingList.clear()
                for (productSnapshot in snapshot.children) {
                    val isTrending = productSnapshot.child("isTrending").getValue(Boolean::class.java) ?: false
                    if (isTrending) {
                        val nama = productSnapshot.child("name").getValue(String::class.java) ?: ""
                        val description = productSnapshot.child("description").getValue(String::class.java) ?: ""
                        val harga = productSnapshot.child("price").getValue(Int::class.java) ?: 0
                        val imageUrl = productSnapshot.child("gambarRes").getValue(String::class.java) ?: "" // Ambil URL gambar

                        // Tambahkan ke list
                        trendingList.add(
                            DataClass(
                                dataImage = imageUrl, // Simpan URL gambar
                                dataTitle = nama,
                                dataPrice = harga.toString(), // Harga sebagai String
                                dataDesc = description,
                                dataDetailImage = imageUrl // URL untuk gambar detail
                            )
                        )
                    }
                }
                trendingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

}
