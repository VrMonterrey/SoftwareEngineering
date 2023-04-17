package com.example.softwareengineering

import PosilkiAdapter
import ProductAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareengineering.model.Posilki
import com.example.softwareengineering.model.Skladnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListOfPosilkiActivity : AppCompatActivity(), PosilkiAdapter.PosilkiAdapterListener {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
    private lateinit var goback: ImageButton
    private lateinit var remove: ImageButton
    private lateinit var dishAdapter: PosilkiAdapter
    private lateinit var dishList: MutableList<Posilki>
    private lateinit var dishRecyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var dishRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_posilki)

        dishRecyclerView = findViewById(R.id.dishRecyclerView)
        dishAdapter = PosilkiAdapter(mutableListOf(), this)
        dishRecyclerView.adapter = dishAdapter

        database = FirebaseDatabase.getInstance()
        dishRef = database.getReference("dishes")

        dishList = mutableListOf()

        dishRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dishList.clear()
                val posilki = mutableListOf<Posilki>()
                for (posilekSnapshot in snapshot.children) {
                    val product = posilekSnapshot.getValue(Posilki::class.java)
                    product?.let {
                        posilki.add(it)
                    }
                }
                dishList.addAll(posilki)
                dishAdapter.updateData(posilki)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        goback = findViewById(R.id.goback_btn)

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

        goback.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext, PosilkiActivity::class.java)
            startActivity(intent)
            finish()
        })

        dishAdapter.setOnDeleteClickListener(object : PosilkiAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                val dish = dishList[position]
                dish.id?.let {
                    val dishRef = database.getReference("dishes/$it")
                    dishRef.removeValue()
                }
            }
        })

    }

    override fun onDeleteClick(position: Int) {
        val dish = dishList[position]
        dish.id?.let {
            val dishRef = database.getReference("dishes/$it")
            dishRef.removeValue().addOnSuccessListener {
                Toast.makeText(this, "Posiłek został pomyślnie usunięty", Toast.LENGTH_SHORT).show()
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

    override fun onEditClick(position: Int) {
        val dish = dishList[position]

        val intent = Intent(this, EditDishActivity::class.java)
        intent.putExtra("posilek", dish.id)
        startActivity(intent)
    }

}