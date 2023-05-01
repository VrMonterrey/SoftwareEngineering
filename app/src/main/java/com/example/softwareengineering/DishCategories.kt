package com.example.softwareengineering

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.adapter.SkladnikiToChooseAdapter
import com.example.softwareengineering.model.Posilki
import com.example.softwareengineering.model.ProductCategory
import com.example.softwareengineering.model.Skladnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DishCategories : AppCompatActivity() {
    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var categoryName: EditText
    private lateinit var addButton: ImageButton
    private lateinit var dialogButton: Button
    private lateinit var kategorieArr: TextView

    private lateinit var adapter: PosilkiToChooseAdapter

    private var selectedDishes: List<Posilki> = emptyList()

    private lateinit var chooseImageButton: Button

    private lateinit var getContent: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_categories)

        dialogButton = findViewById(R.id.dialog_btn)
        dialogButton.setOnClickListener{ showCustomDialog() }

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)

        addButton = findViewById(R.id.submit_btn)

        val database = Firebase.database.reference



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

        kategorieArr = findViewById(R.id.kategorie_arr_btn)
        kategorieArr.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)
        val recyclerView = dialogLayout.findViewById<RecyclerView>(R.id.ingredients_rv)

        val database = FirebaseDatabase.getInstance()
        val dishesRef = database.getReference("dishes")
        val dishesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dishes = dataSnapshot.children.mapNotNull { it.getValue(Posilki::class.java) }
                adapter.setData(dishes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadIngredients:onCancelled", databaseError.toException())
            }
        }
        dishesRef.addValueEventListener(dishesListener)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val dishes = mutableListOf<Skladnik>()
        adapter = PosilkiToChooseAdapter(dishes) { dish ->
            if (dish.checked) {
                dishes.add(dish)
            } else {
                dishes.remove(dish)
            }
        }

        recyclerView.adapter = adapter


        builder.setTitle("Wybierz składniki")
            .setMessage("Kliknij checkbox'a żeby dodać posilek \nNazwa | kalorii | białko | weglewodany | tłuszcz")
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, which ->
                selectedDishes = adapter.getData().filter { it.checked }
                Log.d(ContentValues.TAG, "Selected products: $selectedDishes")
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }
            .create()
            .show()

        addButton.setOnClickListener {
            categoryName = findViewById<EditText>(R.id.name_edit_text)

            val name = categoryName.text.toString()

            val database = Firebase.database.reference

            val currentUser = FirebaseAuth.getInstance().currentUser
            val currentUserId = currentUser?.uid

            val cat = ProductCategory(
                id = database.child("categories").push().key,
                name = name,
                dishes = selectedDishes,
                userId = currentUserId
            )

            if (cat.id != null) {
                database.child("categories").child(cat.id!!).setValue(cat).addOnSuccessListener {
                    Toast.makeText(this, "Nowa kategoria dodana pomyślnie", Toast.LENGTH_SHORT).show()
                    categoryName.text.clear()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Błąd podczas dodawania kategorii: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}