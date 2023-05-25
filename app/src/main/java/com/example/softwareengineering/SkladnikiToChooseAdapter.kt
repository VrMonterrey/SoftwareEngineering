package com.example.softwareengineering.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.R
import com.example.softwareengineering.model.SkladPosilku
import com.example.softwareengineering.model.Skladnik

class SkladnikiToChooseAdapter(
    private val products: MutableList<Skladnik>,
    private val posilkiId: String,
    private val onProductSelected: (Skladnik) -> Unit
) : RecyclerView.Adapter<SkladnikiToChooseAdapter.IngredientViewHolder>() {

    private val skladPosilkuList: MutableList<SkladPosilku> = mutableListOf()
    private val amountMap: MutableMap<String?, Int> = mutableMapOf()
    fun setData(newProducts: List<Skladnik>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun getData(): List<Skladnik> {
        return products
    }
    fun getSkladPosilkuList(): List<SkladPosilku> {
        return skladPosilkuList
    }

    fun getAmountMap(): Map<String?, Int> {
        return amountMap
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skladnki_to_choose_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        // Set the amount from the map for the product
        holder.amountNumber.value = amountMap[product.id] ?: 0
        holder.amountNumber.setOnValueChangedListener { _, _, newVal ->
            // Update the amount in the map when the value changes
            amountMap[product.id] = newVal
        }
    }

    override fun getItemCount(): Int = products.size

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val kaloriiTextView: TextView = itemView.findViewById(R.id.kalorii_edit_text)
        private val proteinsTextView: TextView = itemView.findViewById(R.id.product_proteins)
        private val carbsTextView: TextView = itemView.findViewById(R.id.product_carbs)
        private val fatsTextView: TextView = itemView.findViewById(R.id.product_fats)
        private val checkBox: CheckBox = itemView.findViewById(R.id.ingredientCheckBox)
        val amountNumber: NumberPicker = itemView.findViewById(R.id.numberPicker)

        fun bind(product: Skladnik) {
            nameTextView.text = product.name
            kaloriiTextView.text = product.calories.toString()
            proteinsTextView.text = product.protein.toString()
            carbsTextView.text = product.carbs.toString()
            fatsTextView.text = product.fat.toString()
            amountNumber.minValue = 0
            amountNumber.maxValue = 200

            checkBox.isChecked = skladPosilkuList.any { it.skladnikId == product.id }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Add a SkladPosilku item if the checkbox is checked
                    val skladPosilku = product.id?.let {
                        SkladPosilku(
                            posilkiId = posilkiId,
                            skladnikId = it,
                            amount = amountMap[product.id] ?: 0
                        )
                    }
                    if (skladPosilku != null) {
                        skladPosilkuList.add(skladPosilku)
                    }
                } else {
                    // Remove the SkladPosilku item if the checkbox is unchecked
                    val skladPosilkuToRemove = skladPosilkuList.find { it.skladnikId == product.id }
                    skladPosilkuList.remove(skladPosilkuToRemove)
                }
            }
        }
    }
}

