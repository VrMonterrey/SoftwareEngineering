package com.example.softwareengineering.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.R
import com.example.softwareengineering.model.Skladnik

class SkladnikiToChooseAdapter(
    private val products: MutableList<Skladnik>,
    private val onProductSelected: (Skladnik) -> Unit
) : RecyclerView.Adapter<SkladnikiToChooseAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skladnki_to_choose_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val kaloriiTextView: TextView = itemView.findViewById(R.id.kalorii_edit_text)
        private val proteinsTextView: TextView = itemView.findViewById(R.id.product_proteins)
        private val carbsTextView: TextView = itemView.findViewById(R.id.product_carbs)
        private val fatsTextView: TextView = itemView.findViewById(R.id.product_fats)
        private val checkBox: CheckBox = itemView.findViewById(R.id.ingredientCheckBox)

        fun bind(product: Skladnik) {
            nameTextView.text = product.name
            kaloriiTextView.text = product.calories.toString()
            proteinsTextView.text = product.protein.toString()
            carbsTextView.text = product.carbs.toString()
            fatsTextView.text = product.fat.toString()

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = false

            itemView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
                onProductSelected(product)
            }
        }
    }
}
