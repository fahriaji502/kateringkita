package com.example.bismillah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(private var foodList: List<String>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.foodName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foodName.text = foodList[position]
    }

    override fun getItemCount(): Int = foodList.size

    // Fungsi untuk memperbarui data dalam RecyclerView
    fun updateList(newList: List<String>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
