package com.example.softwareengineering

import ProductAdapterDishDetails
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softwareengineering.model.Comment
import com.example.softwareengineering.model.Posilki
import com.example.softwareengineering.model.Skladnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DishDetailActivity : AppCompatActivity(), ProductAdapterDishDetails.ProductAdapterDishDetailsListener {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var goback: ImageButton

    private lateinit var nameField: TextView
    private lateinit var categoryField: TextView
    private lateinit var quantityField: TextView
    private lateinit var dishImage: ImageView
    private lateinit var kcal: TextView
    private lateinit var proteins: TextView
    private lateinit var carbs: TextView
    private lateinit var fats: TextView

    private lateinit var fullkcal: TextView
    private lateinit var fullproteins: TextView
    private lateinit var fullcarbs: TextView
    private lateinit var fullfats: TextView

    private lateinit var rating_spn: Spinner

    private lateinit var productAdapter: ProductAdapterDishDetails
    private lateinit var productList: MutableList<Skladnik>
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var database2: FirebaseDatabase
    private lateinit var productRef: DatabaseReference

    private lateinit var edit_text: EditText

    private var dishName: String? = ""
    private var dishCategory: String? = ""
    private var dishQuantity: Int? = 1

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentList: MutableList<Comment>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_detail)

        nameField = findViewById(R.id.dish_name)
        dishImage = findViewById(R.id.dish_image)
        kcal = findViewById(R.id.dish_kcal)
        proteins = findViewById(R.id.dish_proteins)
        carbs = findViewById(R.id.dish_carbs)
        fats = findViewById(R.id.dish_fats)

//        fullkcal = findViewById(R.id.full_kcal)
//        fullproteins = findViewById(R.id.full_proteins)
//        fullcarbs = findViewById(R.id.full_carbs)
//        fullfats = findViewById(R.id.full_fats)

        // Initialize Firebase database
        val database = Firebase.database.reference

        // Get dish ID from intent
        val dishId = intent.getStringExtra("posilek") ?: ""

        //Reading dish
        database.child("dishes").child(dishId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dish = snapshot.getValue(Posilki::class.java)
                nameField.text = dish?.name
                dishName = dish?.name
                dishCategory = dish?.category
                dishQuantity = dish?.quantity
                Glide.with(applicationContext)
                    .load(dish?.photoUrl)
                    .into(dishImage)
                dishImage.background = null;
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadDish:onCancelled", error.toException())
            }
        })

        //List of "składniki"
        productRecyclerView = findViewById(R.id.productRecyclerView)
        productAdapter = ProductAdapterDishDetails(mutableListOf(), this)
        productRecyclerView.adapter = productAdapter

        database2 = FirebaseDatabase.getInstance()
        productRef = database2.getReference("products")

        productList = mutableListOf()

        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                val products = mutableListOf<Skladnik>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Skladnik::class.java)
                    product?.let {
                        products.add(it)
                    }
                }
                productList.addAll(products)
                productAdapter.updateData(products)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })

        //Rating
        val ratings = arrayOf(1, 2, 3, 4, 5)
        var selectedItem = 1
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratings)
        rating_spn = findViewById(R.id.ratingSpinner)
        rating_spn.adapter = adapter

        rating_spn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedItem = 1
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = rating_spn.selectedItem.toString().toInt()
            }
        }

        //nameField.text = dishName
        goback = findViewById<ImageButton>(R.id.goback_btn)

        //Menu navigation
        goback.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })

        val addButton = findViewById<Button>(R.id.submit_btn)
        addButton.setOnClickListener {

            edit_text = findViewById<EditText>(R.id.name_edit_text)

            val text = edit_text.text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser
            val currentUserId = currentUser?.uid

            val database = Firebase.database.reference
            val commentId = database.child("dishes").child(dishId).child("comments").push().key
            val comment = currentUserId?.let { it1 ->
                Comment(
                    id = commentId,
                    text = text,
                    ocena = selectedItem,
                    userId = it1,
                    posilekId = dishId
                )
            }

            if (comment != null) {
                if (comment.id != null) {
                    val commentsRef = database.child("dishes").child(dishId).child("comments")
                    val newCommentRef = commentsRef.push()
                    newCommentRef.setValue(comment).addOnSuccessListener {
                        Toast.makeText(this, "Nowy komentarz dodany pomyślnie", Toast.LENGTH_SHORT).show()
                        edit_text.text.clear()
                    }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Błąd podczas dodawania komentarza: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

        }
    }

    override fun onDeleteClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onEditClick(position: Int) {
        TODO("Not yet implemented")
    }
}