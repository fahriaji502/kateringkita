package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryOrderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNotFound: TextView
    private lateinit var adapter: AdapterHistoryOrder
    private val orderHistoryList = mutableListOf<DataClass.OrderHistory>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)

        recyclerView = findViewById(R.id.rvHistory)
        tvNotFound = findViewById(R.id.tvNotFound)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }

        adapter = AdapterHistoryOrder(orderHistoryList,
            onPesanLagi = { order ->
                pesanLagi(order)
            },
            onHapus = { order ->
                hapusRiwayat(order)
            }
        )

        recyclerView.adapter = adapter
        database = FirebaseDatabase.getInstance().getReference("orderHistory")

        fetchOrderHistory()
    }

    private fun fetchOrderHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderHistoryList.clear()
                for (data in snapshot.children) {
                    val order = data.getValue(DataClass.OrderHistory::class.java)
                    if (order != null) {
                        orderHistoryList.add(order)
                    }
                }
                toggleEmptyState()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@HistoryOrderActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun toggleEmptyState() {
        if (orderHistoryList.isEmpty()) {
            tvNotFound.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvNotFound.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun pesanLagi(order: DataClass.OrderHistory) {
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("TOTAL_HARGA", order.totalHarga)
        intent.putParcelableArrayListExtra(
            "DAFTAR_PESANAN",
            arrayListOf(DataClass.Makanan(order.namaProduk, order.totalHarga.toString(), ""))
        )
        startActivity(intent)
    }

    private fun hapusRiwayat(order: DataClass.OrderHistory) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val orderId = order.idTransaksi ?: return
        database.child(userId).child(orderId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Riwayat berhasil dihapus", Toast.LENGTH_SHORT).show()
                orderHistoryList.remove(order)
                adapter.notifyDataSetChanged()
                toggleEmptyState()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus riwayat: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
