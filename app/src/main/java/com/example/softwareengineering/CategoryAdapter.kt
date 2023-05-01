import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.R
import com.example.softwareengineering.model.Posilki
import com.example.softwareengineering.model.ProductCategory

class CategoryAdapter(
    private var catList: MutableList<ProductCategory>,
    private val listener: CategoryAdapterListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_category_item, parent, false)
        return CategoryViewHolder(itemView)
    }


    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        this.onDeleteClickListener = listener
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = catList[position]

        holder.nameTextView.text = currentItem.name

    }

    override fun getItemCount() = catList.size

    fun updateData(newCat: MutableList<ProductCategory>) {
        catList.clear()
        catList = newCat
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.category_name)
        val deleteButton: AppCompatImageView = itemView.findViewById(R.id.remove_btn)
        val editBotton: AppCompatImageView = itemView.findViewById(R.id.edit_btn)

        private var currentCat: ProductCategory? = null

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

        }

        fun bind(cats: ProductCategory) {
            currentCat = cats

            nameTextView.text = cats.name

        }
    }

    interface CategoryAdapterListener {
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
        fun onCommentClick(position: Int)
        fun onCatClick(position: Int)
    }
}