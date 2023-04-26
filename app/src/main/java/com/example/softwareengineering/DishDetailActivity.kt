package com.example.softwareengineering

import ProductAdapter
import ProductAdapterDishDetails
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    private lateinit var rating_spn: Spinner

    private lateinit var productAdapter: ProductAdapterDishDetails
    private lateinit var productList: MutableList<Skladnik>
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var database2: FirebaseDatabase
    private lateinit var productRef: DatabaseReference

    private var dishName: String? = ""
    private var dishCategory: String? = ""
    private var dishQuantity: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_detail)

        nameField = findViewById(R.id.dish_name)
        dishImage = findViewById(R.id.dish_image)
        kcal = findViewById(R.id.dish_kcal)
        proteins = findViewById(R.id.dish_proteins)
        carbs = findViewById(R.id.dish_carbs)
        fats = findViewById(R.id.dish_fats)

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
        val ratings = arrayOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratings)
        rating_spn = findViewById(R.id.ratingSpinner)
        rating_spn.adapter = adapter

        rating_spn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }

        //nameField.text = dishName
        goback = findViewById(R.id.goback_btn)

        //Menu navigation
        goback.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    override fun onDeleteClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onEditClick(position: Int) {
        TODO("Not yet implemented")
    }
}