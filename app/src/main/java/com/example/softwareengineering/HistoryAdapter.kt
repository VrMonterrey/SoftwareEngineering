package com.example.softwareengineering

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import model.DishCategory
import model.Eaten
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var posilkiList: MutableList<Eaten>,
    private val listener: HistoryAdapterListener
) : RecyclerView.Adapter<HistoryAdapter.PosilkiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosilkiViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return PosilkiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosilkiViewHolder, position: Int) {
        val currentItem = posilkiList[position]


        holder.nameTextView.text = currentItem.dishName

        val photoUrl = currentItem?.photoUrl
        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .into(holder.photoImageView)

        val timeInMillis = currentItem.date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(timeInMillis)
        val formattedDate = dateFormat.format(date)
        holder.timeTextView.text = formattedDate

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("categories").child(currentItem.category)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val category = snapshot.getValue(DishCategory::class.java)
                if (category != null) {
                    holder.catTextView.text = "Kategoria: ${category.name}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    override fun getItemCount() = posilkiList.size

    fun updateData(newPosilki: MutableList<Eaten>) {
        posilkiList.clear()
        posilkiList.addAll(newPosilki)
        notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class PosilkiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: AppCompatImageView = itemView.findViewById(R.id.image_holder)
        val nameTextView: TextView = itemView.findViewById(R.id.posilki_name)
        val timeTextView: TextView = itemView.findViewById(R.id.posilki_time)
        val catTextView: TextView = itemView.findViewById(R.id.posilki_cat)
    }

    interface HistoryAdapterListener {

    }
}