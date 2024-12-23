package com.example.bismillah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AdapterHistoryOrder(
    private val orderHistoryList: MutableList<DataClass.OrderHistory>,
    private val onPesanLagi: (DataClass.OrderHistory) -> Unit,
    private val onHapus: (DataClass.OrderHistory) -> Unit
) : RecyclerView.Adapter<AdapterHistoryOrder.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLogo: ImageView = view.findViewById(R.id.imgLogo) // Gambar produk/logo
        val tvNama: TextView = view.findViewById(R.id.tvNama) // Nama produk
        val tvPrice: TextView = view.findViewById(R.id.tvPrice) // Harga total
        val tvJml: TextView = view.findViewById(R.id.tvJml) // Jumlah pesanan
        val tvStatus: TextView = view.findViewById(R.id.tvStatus) // Metode pembayaran
        val tvDate: TextView = view.findViewById(R.id.tvDate) // Tanggal transaksi
        val btnPesanLagi: MaterialButton = view.findViewById(R.id.btnPesanLagi) // Tombol Pesan Lagi
        val btnHapus: MaterialButton = view.findViewById(R.id.btndetele) // Tombol Hapus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderHistoryList[position]

        // Atur logo default
        holder.imgLogo.setImageResource(R.drawable.ic_history) // Pastikan drawable tersedia

        // Tampilkan Nama Produk
        holder.tvNama.text = order.namaProduk
        holder.tvPrice.text = "Rp ${String.format("%,d", order.totalHarga).replace(",", ".")}"
        holder.tvJml.text = "Jumlah: ${order.quantity ?: "N/A"}" // Gunakan "N/A" jika tidak ada jumlah
        holder.tvStatus.text = "Metode: ${order.metodePembayaran}"
        holder.tvDate.text = "Tanggal: ${order.tanggalTransaksi}"

        // Atur tombol "Pesan Lagi"
        holder.btnPesanLagi.setOnClickListener {
            onPesanLagi(order)
        }

        // Atur tombol "Hapus"
        holder.btnHapus.setOnClickListener {
            onHapus(order)
        }
    }

    override fun getItemCount() = orderHistoryList.size

    // Fungsi untuk menghapus item dari UI
    fun removeItem(order: DataClass.OrderHistory) {
        val position = orderHistoryList.indexOf(order)
        if (position != -1) {
            orderHistoryList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
