package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var rvTrending: RecyclerView
    private lateinit var trendingAdapter: AdapterTrending
    private lateinit var database: DatabaseReference
    private lateinit var cvEditAlamat: CardView
    private lateinit var cvHistory: CardView
    private lateinit var tvEditAlamat: TextView
    private lateinit var tvSource: TextView
    private lateinit var cvCategories: CardView
    private lateinit var searchView: SearchView
    private lateinit var profileIcon: ImageView

    private var trendingList: ArrayList<DataClass> = arrayListOf()
    private var filteredList: ArrayList<DataClass> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Views
        profileIcon = findViewById(R.id.profile)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi RecyclerView Trending
        rvTrending = findViewById(R.id.rvTrending)
        rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = AdapterTrending(filteredList)
        rvTrending.adapter = trendingAdapter

        // Fetch data trending dari Firebase
        fetchTrendingData()

        // Listener klik item untuk pindah ke TrendingActivity
        trendingAdapter.onItemClick = { item ->
            val intent = Intent(this, TrendingActivity::class.java)
            intent.putExtra("TRENDING_TITLE", item.dataTitle)
            startActivity(intent)
        }

        // Pencarian
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterTrendingData(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterTrendingData(it) }
                return true
            }
        })

        // Inisialisasi CardView lainnya
        cvCategories = findViewById(R.id.cvCategories)
        cvCategories.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

        cvEditAlamat = findViewById(R.id.cvEditAlamat)
        cvEditAlamat.setOnClickListener {
            val intent = Intent(this, AlamatActivity::class.java)
            startActivity(intent)
        }

        cvHistory = findViewById(R.id.cvHistory)
        cvHistory.setOnClickListener {
            val intent = Intent(this, HistoryOrderActivity::class.java)
            startActivity(intent)
        }
    }

    // Mengambil data trending dari Firebase
    private fun fetchTrendingData() {
        database = FirebaseDatabase.getInstance().getReference("products")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trendingList.clear()
                for (productSnapshot in snapshot.children) {
                    val isTrending = productSnapshot.child("isTrending").getValue(Boolean::class.java) ?: false
                    if (isTrending) {
                        val nama = productSnapshot.child("name").getValue(String::class.java) ?: ""
                        val harga = productSnapshot.child("price").getValue(Int::class.java)?.toString() ?: "0"
                        val description = productSnapshot.child("description").getValue(String::class.java) ?: ""
                        val gambarUrl = productSnapshot.child("gambarRes").getValue(String::class.java) ?: ""

                        // Debugging log
                        Log.d("Firebase", "Trending Item: Nama=$nama, Harga=Rp $harga, Gambar=$gambarUrl")

                        trendingList.add(
                            DataClass(
                                dataImage = gambarUrl, // URL gambar
                                dataTitle = nama,
                                dataPrice = "Rp $harga",
                                dataDesc = description,
                                dataDetailImage = gambarUrl
                            )
                        )
                    }
                }
                filteredList.clear()
                filteredList.addAll(trendingList)
                trendingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    // Memfilter data berdasarkan input pencarian
    private fun filterTrendingData(query: String) {
        val filtered = trendingList.filter { it.dataTitle.contains(query, ignoreCase = true) }
        filteredList.clear()
        filteredList.addAll(filtered)
        trendingAdapter.notifyDataSetChanged()
    }

    // Logout pengguna
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
