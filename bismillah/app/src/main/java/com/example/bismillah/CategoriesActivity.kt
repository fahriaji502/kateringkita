package com.example.bismillah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

class CategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Menghubungkan CardView dari XML
        val cvCategory1: CardView = findViewById(R.id.cvCategory1)
        val cvCategory2: CardView = findViewById(R.id.cvCategory2)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke aktivitas sebelumnya
        }


        // Listener untuk Category 1
        cvCategory1.setOnClickListener {
            // Pindah ke OrderActivity dengan membawa data "Category 1"
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("ORDER_CATEGORY", "Makanan")
            startActivity(intent)
            Toast.makeText(this, "Kategori Makanan Dipilih", Toast.LENGTH_SHORT).show()
        }

        // Listener untuk Category 2
        cvCategory2.setOnClickListener {
            // Pindah ke OrderActivity dengan membawa data "Category 2"
            val intent = Intent(this, MinumanActivity::class.java)
            intent.putExtra("ORDER_CATEGORY", "Minuman")
            startActivity(intent)
            Toast.makeText(this, "Kategori Minuman Dipilih", Toast.LENGTH_SHORT).show()
        }
    }
}
