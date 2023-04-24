import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.R
import com.example.softwareengineering.model.Posilki

class PosilkiAdapter(
    private var posilkiList: MutableList<Posilki>,
    private val listener: PosilkiAdapterListener
) :
    RecyclerView.Adapter<PosilkiAdapter.PosilkiViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosilkiViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.posilki_item, parent, false)
        return PosilkiViewHolder(itemView)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        this.onDeleteClickListener = listener
    }

    override fun onBindViewHolder(holder: PosilkiViewHolder, position: Int) {
        val currentItem = posilkiList[position]

        holder.nameTextView.text = currentItem.name
        holder.categoryTextView.text = currentItem.category
        holder.quantityTextView.text = currentItem.quantity.toString()

        val photoUrl = currentItem.photoUrl
        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .into(holder.photoImageView)
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
        val deleteButton: AppCompatImageView = itemView.findViewById(R.id.remove_btn)
        val editBotton: AppCompatImageView = itemView.findViewById(R.id.edit_btn)
        val photoImageView: AppCompatImageView = itemView.findViewById(R.id.image_holder)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
            editBotton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position)
                }
            }

            photoImageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDishClick(position)
                }
            }
        }
    }

    interface PosilkiAdapterListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
        fun onCommentClick(position: Int)
        fun onDishClick(position: Int)
    }
}