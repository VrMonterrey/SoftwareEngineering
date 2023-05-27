package com.example.softwareengineering

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.adapter.SkladnikiToChooseAdapter
import com.example.softwareengineering.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream


class PosilkiActivity : AppCompatActivity() {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var posilkiarr: TextView

    private lateinit var dishName: EditText
    private lateinit var addButton: ImageButton
    private lateinit var dialogButton: Button

    private lateinit var adapter: SkladnikiToChooseAdapter

    private var amounts: Map<String?, Int> = mutableMapOf()

    private lateinit var imageView: ImageView
    private lateinit var chooseImageButton: Button
    private var photoUrl: String = ""

    private lateinit var getContent: ActivityResultLauncher<String>

//    private lateinit var kategoriaText: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryList: MutableList<ProductCategory>
    private var selectedCategory: String = ""

    private lateinit var databaseReference: DatabaseReference


    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posilki)

        dialogButton = findViewById(R.id.dialog_btn)
        dialogButton.setOnClickListener{ showCustomDialog() }

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        posilkiarr = findViewById(R.id.posilki_arr_btn)

        addButton = findViewById(R.id.submit_btn)


        //Choose category
//        kategoriaText = findViewById(R.id.kategoriaText)
        categorySpinner = findViewById(R.id.categorySpinner)

        databaseReference = FirebaseDatabase.getInstance().reference.child("categories")
        categoryList = mutableListOf()

        val categoriesorSpinner: MutableList<String> = mutableListOf()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()

                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(ProductCategory::class.java)
                    category?.let {
                        categoryList.add(it)
                    }
                }

                for (category in categoryList) {
                    categoriesorSpinner.add(category.name)
                }

                val adapter = ArrayAdapter(this@PosilkiActivity, R.layout.spinner_item_layout, categoriesorSpinner)
                categorySpinner.adapter = adapter

//                kategoriaText.text = "Kategoria"

                categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedCategory = categoriesorSpinner[position]
//                        kategoriaText.text = selectedCategory
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //Choose image from gallery
        imageView = findViewById<ImageView>(R.id.image_view)
        chooseImageButton = findViewById<Button>(R.id.choose_image_button)

        val storageRef = FirebaseStorage.getInstance().reference.child("images")

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageView.setImageURI(uri)
                val imageRef = storageRef.child(uri.lastPathSegment!!.substringAfterLast("/"))
                val uploadTask = contentResolver?.openInputStream(uri)?.readBytes()?.let { imageRef.putBytes(it) }
                uploadTask?.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        photoUrl = uri.toString()
                        Log.d(TAG, "Image URL: $photoUrl")
                    }
                }
            }
        }

        chooseImageButton.setOnClickListener {
            getContent.launch("image/*")
        }

        //Menu navigation
        home.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        categories.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(applicationContext, CategoriesActivity::class.java)
            startActivity(intent)
            finish()
        })

        logout.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intent: Intent = Intent(applicationContext, login::class.java)
            startActivity(intent)
            finish()
        })

        posilkiarr.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intent: Intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })

    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
        val recyclerView = dialogLayout.findViewById<RecyclerView>(R.id.ingredients_rv)

        recyclerView.apply {
            // Set the max height of the RecyclerView
            val displayMetrics = context.resources.displayMetrics
            layoutParams.height = (displayMetrics.heightPixels * 0.5).toInt() // This sets the max height to 50% of screen height
            requestLayout()
        }

        val database = Firebase.database.reference
        val productsRef = database.child("products")
        val productsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val products = dataSnapshot.children.mapNotNull { it.getValue(Skladnik::class.java) }
                adapter.setData(products)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadIngredients:onCancelled", databaseError.toException())
            }
        }
        productsRef.addValueEventListener(productsListener)

        recyclerView.layoutManager = LinearLayoutManager(this)
        var dishId = database.child("dishes").push().key
        val products = mutableListOf<Skladnik>()
        adapter = dishId?.let {
            SkladnikiToChooseAdapter(products, it) { product ->
                if (product.checked) {
                    products.add(product)
                } else {
                    products.remove(product)
                }
            }
        }!!

        recyclerView.adapter = adapter


        builder.setTitle("Wybierz składniki")
            .setMessage("Nazwa \nKalorie | Białko | Weglewodany | Tłuszcz")
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, which ->
                amounts = adapter.amountMap
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }
            .create()
            .show()


        addButton.setOnClickListener {
            dishName = findViewById<EditText>(R.id.name_edit_text)

            val name = dishName.text.toString()
            val category = selectedCategory

            if (name.isBlank() || category.isBlank() || amounts.isEmpty()) {
                Toast.makeText(this, "Wszystkie pola muszą być wypełnione poprawnie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (photoUrl.isBlank()) {
                Toast.makeText(this, "Pobieram zdjęcie...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val currentUser = FirebaseAuth.getInstance().currentUser
            val currentUserId = currentUser?.uid

            val categoriesRef = database.child("categories")
            val query = categoriesRef.orderByChild("name").equalTo(category)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Category found, retrieve the ID
                        val categorySnapshot = dataSnapshot.children.first()
                        val categoryId = categorySnapshot.key


                        val collectionRef = database.child("composition")

                        for ((skladnikId, amount) in amounts) {
                            val skladPosilku = skladnikId?.let { it1 ->
                                SkladPosilku(
                                    posilkiId = dishId,
                                    skladnikId = it1,
                                    amount = amount
                                )
                            }

                            collectionRef.push().setValue(skladPosilku)
                        }

                        val dish = categoryId?.let { it1 ->
                            Posilki(
                                id = dishId,
                                name = name,
                                category = it1,
                                photoUrl = photoUrl,
                                comments = null,
                                userId = currentUserId
                            )
                        }

                        if (dish != null) {
                            if (dish.id != null) {
                                database.child("dishes").child(dish.id!!).setValue(dish)
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Nowy posiłek dodany pomyślnie", Toast.LENGTH_SHORT).show()
                                        dishName.text.clear()
                                        imageView.setImageBitmap(null)

                                        val intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Błąd podczas dodawania posiłka: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    } else {
                        // Category not found
                        Toast.makeText(applicationContext, "Category not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error case
                    Log.e(TAG, "Error retrieving category: ${databaseError.message}")
                }
            })
        }
    }
}
