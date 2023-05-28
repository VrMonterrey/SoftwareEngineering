package com.example.softwareengineering

import NotificationUtils
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.softwareengineering.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DaylistActivity : AppCompatActivity(), DailyAdapter.PosilkiAdapterListener {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var profile: ImageButton

    private lateinit var monday: ImageButton
    private lateinit var tuesday: ImageButton
    private lateinit var wednesday: ImageButton
    private lateinit var thursday: ImageButton
    private lateinit var friday: ImageButton
    private lateinit var saturday: ImageButton
    private lateinit var sunday: ImageButton

    private lateinit var dishAdapter: DailyAdapter
    private lateinit var dishList: MutableList<DailyNutrition>
    private lateinit var dishRecyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var dishRef: DatabaseReference
    private var selectedDay = 1


    // Function to handle day selection
    @SuppressLint("ResourceAsColor")
    private fun selectDay(day: Int, selectedButton: ImageButton) {

        val white = ColorStateList.valueOf(Color.WHITE)
        val green = ColorStateList.valueOf(R.color.yellow)

        monday.backgroundTintList = white
        tuesday.backgroundTintList = white
        wednesday.backgroundTintList = white
        thursday.backgroundTintList = white
        friday.backgroundTintList = white
        saturday.backgroundTintList = white
        sunday.backgroundTintList = white

        selectedButton.backgroundTintList = green

        selectedDay = day

        updateDishList()
    }

    // Function to update the dish list based on the selected day
    private fun updateDishList() {
        dishList.clear()
        val posilki = mutableListOf<DailyNutrition>()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        dishRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (posilekSnapshot in snapshot.children) {
                    val product = posilekSnapshot.getValue(DailyNutrition::class.java)

                    if (product != null && product.userId == currentUserId && product.day == selectedDay) {
                        posilki.add(product)
                    }
                }

                posilki.sortBy { it.time }
                dishList.addAll(posilki)
                dishAdapter.updateData(posilki)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }


    fun calculateMacro(posilek: Posilki, callback: (MacroNutrients) -> Unit) {
        val posilkiId = posilek.id
        val databaseReference = FirebaseDatabase.getInstance().reference.child("composition")

        var sumCalories: Double = 0.0
        var sumProteins: Double = 0.0
        var sumCarbs: Double = 0.0
        var sumFats: Double = 0.0
        var amount: Int = 0
        val skladnikIds: MutableList<String> = mutableListOf()

        databaseReference.orderByChild("posilkiId").equalTo(posilkiId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (skladPosilkiSnapshot in snapshot.children) {
                        val skladPosilki = skladPosilkiSnapshot.getValue(SkladPosilku::class.java)
                        skladPosilki?.let {
                            val skladnikId = it.skladnikId
                            val amountInGrams = it.amount

                            skladnikIds.add(skladnikId)
                            amount += amountInGrams

                            val skladnikReference =
                                FirebaseDatabase.getInstance().reference.child("products")
                                    .child(skladnikId)
                            skladnikReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(skladnikSnapshot: DataSnapshot) {
                                    val skladnik = skladnikSnapshot.getValue(Skladnik::class.java)
                                    skladnik?.let { skladnik ->
                                        val skladnikCaloriesPer100g = skladnik.calories
                                        val skladnikProteinsPer100g = skladnik.protein
                                        val skladnikCarbsPer100g = skladnik.carbs
                                        val skladnikFatsPer100g = skladnik.fat

                                        val skladnikCalories: Double =
                                            (skladnikCaloriesPer100g * amountInGrams) / 100
                                        val skladnikProteins: Double =
                                            (skladnikProteinsPer100g * amountInGrams) / 100
                                        val skladnikCarbs: Double =
                                            (skladnikCarbsPer100g * amountInGrams) / 100
                                        val skladnikFats: Double =
                                            (skladnikFatsPer100g * amountInGrams) / 100

                                        sumCalories += skladnikCalories
                                        sumProteins += skladnikProteins
                                        sumCarbs += skladnikCarbs
                                        sumFats += skladnikFats
                                    }



                                    val nutrients = MacroNutrients(sumCalories, sumProteins, sumCarbs, sumFats, amount)
                                    callback(nutrients)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daylist)


        monday = findViewById(R.id.monday)
        tuesday = findViewById(R.id.tuesday)
        wednesday = findViewById(R.id.wednesday)
        thursday = findViewById(R.id.thursday)
        friday = findViewById(R.id.friday)
        saturday = findViewById(R.id.saturday)
        sunday = findViewById(R.id.sunday)


        dishRecyclerView = findViewById(R.id.dishRecyclerView)
        val notificationUtils = NotificationUtils()
        dishAdapter = DailyAdapter(mutableListOf(), this, notificationUtils)

        dishRecyclerView.setHasFixedSize(true)
        dishRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        dishRecyclerView.adapter = dishAdapter


        database = FirebaseDatabase.getInstance()
        dishRef = database.getReference("scheduled")
        dishList = mutableListOf()


        monday.setOnClickListener {
            selectDay(1, monday)
        }
        tuesday.setOnClickListener {
            selectDay(2, tuesday)
        }
        wednesday.setOnClickListener {
            selectDay(3, wednesday)
        }
        thursday.setOnClickListener {
            selectDay(4, thursday)
        }
        friday.setOnClickListener {
            selectDay(5, friday)
        }
        saturday.setOnClickListener {
            selectDay(6, saturday)
        }
        sunday.setOnClickListener {
            selectDay(7, sunday)
        }



        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        profile = findViewById(R.id.profile_button)


        //goback = findViewById(R.id.goback_btn)

        home.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        categories.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, CategoriesActivity::class.java)
            startActivity(intent)
            finish()
        })

        logout.setOnClickListener(View.OnClickListener{
            FirebaseAuth.getInstance().signOut()
            var intent : Intent = Intent(applicationContext, login::class.java)
            startActivity(intent)
            finish()
        })

        profile.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        })


    }

    override fun onDeleteClick(position: Int) {
        val dish = dishList[position]
        dish.id?.let {
            val dishRef = database.getReference("scheduled/$it")
            dishRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Posiłek usunięty z listy na dzień", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Błąd podczas usuwania posiłku: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onSuccessClick(position: Int) {
        val daily = dishList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val database = Firebase.database.reference
        database.child("dishes").child(daily.posilekId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dish = snapshot.getValue(Posilki::class.java)
                    if (dish != null) {
                        calculateMacro(dish) { nutrients ->
                            val eaten = Eaten(
                                id = database.child("eaten").push().key,
                                date = System.currentTimeMillis(),
                                userId = currentUserId.orEmpty(),
                                calories = nutrients.calories,
                                protein = nutrients.proteins,
                                carbs = nutrients.carbs,
                                fat = nutrients.fats
                            )

                            if (eaten.id != null) {
                                database.child("eaten").child(eaten.id!!).setValue(eaten)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@DaylistActivity, "Posiłek spożyty", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@DaylistActivity,
                                            "Błąd podczas dodawania posiłku do spożytych: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "loadDish:onCancelled", error.toException())
                }
            })
    }
    override fun onDishClick(position: Int) {
        val daily = dishList[position]

        val intent = Intent(this, DishDetailActivity::class.java)
        intent.putExtra("posilek", daily.posilekId)
        startActivity(intent)
    }

}