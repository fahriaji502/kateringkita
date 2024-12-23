package com.example.bismillah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MakananAdapter(
    private val makananList: List<DataClass.Makanan>,
    private val onItemChanged: (Int, Int, List<DataClass.Makanan>) -> Unit,
    private val onImageClick: (DataClass.Makanan) -> Unit // Callback untuk klik gambar
) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    private val itemCounts = mutableMapOf<Int, Int>() // Menyimpan jumlah porsi untuk setiap item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val makanan = makananList[position]
        val currentCount = itemCounts[position] ?: 0

        // Mengisi data ke ViewHolder
        holder.namaTextView.text = makanan.nama
        holder.hargaTextView.text = "Rp ${String.format("%,d", makanan.harga.toInt()).replace(",", ".")}"
        holder.jumlahTextView.text = currentCount.toString()

        // Memuat gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(makanan.gambarUrl) // URL gambar dari Firebase
            .into(holder.gambarImageView)

        // Tambahkan listener untuk klik gambar
        holder.gambarImageView.setOnClickListener {
            onImageClick(makanan) // Panggil callback dengan data makanan
        }

        // Tombol Tambah
        holder.tambahButton.setOnClickListener {
            val newCount = (itemCounts[position] ?: 0) + 1
            itemCounts[position] = newCount
            holder.jumlahTextView.text = newCount.toString()
            updateTotals()
        }

        // Tombol Kurang
        holder.kurangButton.setOnClickListener {
            val currentCount = itemCounts[position] ?: 0
            if (currentCount > 0) { // Jangan biarkan jumlah kurang dari 0
                val newCount = currentCount - 1
                itemCounts[position] = newCount
                holder.jumlahTextView.text = newCount.toString()
                updateTotals() // Perbarui total harga dan jumlah item
            }
        }
    }

    override fun getItemCount(): Int = makananList.size

    // Mengupdate total jumlah item dan harga
    private fun updateTotals() {
        val selectedItems = mutableListOf<DataClass.Makanan>()
        var totalItems = 0
        var totalHarga = 0

        itemCounts.forEach { (index, count) ->
            if (count > 0) {
                val makanan = makananList[index]
                totalItems += count
                totalHarga += count * makanan.harga.toInt()
                selectedItems.add(makanan)
            }
        }

        onItemChanged(totalItems, totalHarga, selectedItems)
    }

    // ViewHolder untuk menyimpan referensi elemen UI
    inner class MakananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaTextView: TextView = itemView.findViewById(R.id.tvNamaMakanan)
        val hargaTextView: TextView = itemView.findViewById(R.id.tvHargaMakanan)
        val estimasiTextView: TextView = itemView.findViewById(R.id.tvEstimasiWaktu)
        val gambarImageView: ImageView = itemView.findViewById(R.id.imgMakanan)
        val tambahButton: ImageView = itemView.findViewById(R.id.imageAdd1)
        val kurangButton: ImageView = itemView.findViewById(R.id.imageMinus1)
        val jumlahTextView: TextView = itemView.findViewById(R.id.tvPaket1)
    }
}

