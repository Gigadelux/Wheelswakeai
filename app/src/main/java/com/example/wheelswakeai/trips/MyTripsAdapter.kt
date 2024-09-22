package com.example.wheelswakeai.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelswakeai.R
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */
class MyTripsAdapter(private val itemList: List<trip>) : RecyclerView.Adapter<MyTripsAdapter.MyItemViewHolder>() {

    class MyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.itemName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.itemDescription)
        val timeTextView:TextView = itemView.findViewById(R.id.time)
        val speedTextView:TextView = itemView.findViewById(R.id.speed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.trip, parent, false)
        return MyItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.nameTextView.text = currentItem.date
        holder.descriptionTextView.text = currentItem.description
        holder.timeTextView.text = currentItem.time
        holder.speedTextView.text = currentItem.speed
    }

    override fun getItemCount() = itemList.size
}
