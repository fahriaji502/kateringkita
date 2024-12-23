package com.example.bismillah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterPesanan(private val pesananList: List<DataClass.Pesanan>) :
    RecyclerView.Adapter<AdapterPesanan.PesananViewHolder>() {

    class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaItem)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHargaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pesanan, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]

        // Bersihkan format harga
        val hargaBersih = pesanan.harga
            .replace("Rp", "")  // Hapus 'Rp'
            .replace(".", "")   // Hapus titik
            .trim()

        holder.tvNama.text = pesanan.nama
        holder.tvHarga.text = "Rp ${String.format("%,d", hargaBersih.toInt()).replace(",", ".")}"
    }

    override fun getItemCount(): Int = pesananList.size
}
