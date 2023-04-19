package com.example.softwareengineering

import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.softwareengineering.model.Posilki
import com.example.softwareengineering.model.Skladnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DishDetailActivity : AppCompatActivity() {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var goback: ImageButton

    private lateinit var nameField: TextView
    private lateinit var categoryField: TextView
    private lateinit var quantityField: TextView
    private lateinit var kcal: TextView
    private lateinit var proteins: TextView
    private lateinit var carbs: TextView
    private lateinit var fats: TextView

    private var dishName: String? = ""
    private var dishCategory: String? = ""
    private var dishQuantity: Int? = 1
    private lateinit var dishProducts: List<Skladnik>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish_detail)

        nameField = findViewById(R.id.dish_name)
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
                if (dish != null) {
                    dishProducts = dish.products
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadDish:onCancelled", error.toException())
            }
        })

        //nameField.text = dishName

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        goback = findViewById(R.id.goback_btn)

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

        goback.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, ListOfPosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}