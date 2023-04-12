import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.R
import com.example.softwareengineering.model.Posilki

class PosilkiAdapter(
    private var posilkiList: MutableList<Posilki>,
    private val listener: PosilkiAdapterListener
) : RecyclerView.Adapter<PosilkiAdapter.PosilkiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosilkiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.posilki_item, parent, false)
        return PosilkiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosilkiViewHolder, position: Int) {
        val currentItem = posilkiList[position]

        holder.nameTextView.text = currentItem.name
        holder.categoryTextView.text = currentItem.category
        holder.quantityTextView.text = currentItem.quantity.toString()
    }

    override fun getItemCount() = posilkiList.size

    fun updateData(newPosilki: MutableList<Posilki>) {
        posilkiList.clear()
        posilkiList = newPosilki
        notifyDataSetChanged()
    }

    inner class PosilkiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.posilki_name)
        val categoryTextView: TextView = itemView.findViewById(R.id.posilki_category)
        val quantityTextView: TextView = itemView.findViewById(R.id.posilki_quantity)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(posilkiList[position])
                }
            }
        }
    }

    interface PosilkiAdapterListener {
        fun onItemClick(posilki: Posilki)
    }
}