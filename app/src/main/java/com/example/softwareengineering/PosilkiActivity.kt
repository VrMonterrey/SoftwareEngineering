package com.example.softwareengineering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.softwareengineering.model.Posilki
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.softwareengineering.model.Skladnik

class PosilkiActivity : AppCompatActivity() {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var nameOfProduct: EditText
    private lateinit var spinnerCategory: EditText
    private lateinit var imageUrl: EditText
    private lateinit var ingredientsList: EditText
    private lateinit var ilosc: EditText
    private lateinit var addButton: ImageButton

    private lateinit var skladnikiArr: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posilki)

        val addButton = findViewById<ImageButton>(R.id.submit_btn)
        addButton.setOnClickListener {

            nameOfProduct = findViewById<EditText>(R.id.name_edit_text)
            spinnerCategory = findViewById<EditText>(R.id.category_edit_text)
            imageUrl = findViewById<EditText>(R.id.image_edit_text)
            ingredientsList = findViewById<EditText>(R.id.lista_edit_text)
            ilosc = findViewById<EditText>(R.id.ilosc_edit_text)

            val name = nameOfProduct.text.toString().trim()
            //val category = spinnerCategory.selectedItem.toString()
            val category = spinnerCategory.text.toString().trim()
            val photoUrl = imageUrl.text.toString().trim()
            val ingredients = ingredientsList.text.toString().trim().split(", ")
            val quantity = ilosc.text.toString().toInt()

            if (name.isEmpty() || category.isEmpty() || ingredients.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val database = Firebase.database.reference

            val dish = Posilki(
                id = database.child("posilki").push().key,
                name = name,
                category = category,
                quantity = quantity,
                products = ingredients,
                photoUrl = photoUrl
            )

            if (dish.id != null) {
                database.child("").child(dish.id!!).setValue(dish).addOnSuccessListener {
                    Toast.makeText(this, "Nowy posilek dodany pomyślnie", Toast.LENGTH_SHORT).show()
                    nameOfProduct.text.clear()
                    spinnerCategory.text.clear()
                    imageUrl.text.clear()
                    ingredientsList.text.clear()
                    ilosc.text.clear()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Błąd podczas dodawania posiłka: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        skladnikiArr = findViewById(R.id.skladniki_arr_btn)

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

        skladnikiArr.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(applicationContext, ListOfSkladnikiActivity::class.java)
            startActivity(intent)
            finish()
        })

    }
}