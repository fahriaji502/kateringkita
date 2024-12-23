package com.example.bismillah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdapterTrending(private val dataList: ArrayList<DataClass>) :
    RecyclerView.Adapter<AdapterTrending.ViewHolderClass>() {

    var onItemClick: ((DataClass) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_trending, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        // Memuat gambar langsung dari URL menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.dataImage) // URL gambar
            .into(holder.imgThumb)

        // Set teks untuk judul
        holder.tvPlaceName.text = currentItem.dataTitle

        // Klik item
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumb: ImageView = itemView.findViewById(R.id.imgThumb)
        val tvPlaceName: TextView = itemView.findViewById(R.id.tvPlaceName)
    }
}
