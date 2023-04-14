package com.example.softwareengineering

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.model.Posilki

class PosilkiAdapter(
    private val posilkiList: List<Posilki>,
    private val listener: PosilkiAdapterListener
) : RecyclerView.Adapter<PosilkiAdapter.ViewHolder>() {

    interface PosilkiAdapterListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.posilki_name)
        val categoryTextView: TextView = itemView.findViewById(R.id.posilki_category)
        val quantityTextView: TextView = itemView.findViewById(R.id.posilki_quantity)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.posilki_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPosilki = posilkiList[position]
        holder.nameTextView.text = currentPosilki.name
        holder.categoryTextView.text = currentPosilki.category
        holder.quantityTextView.text = currentPosilki.quantity.toString()

        holder.itemView.setOnLongClickListener {
            listener.onDeleteClick(position)
            true
        }
    }

    override fun getItemCount() = posilkiList.size
}