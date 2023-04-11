package com.example.softwareengineering

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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
    private lateinit var dishName: EditText
    private lateinit var dishCategory: EditText
    private lateinit var dishImage: EditText
    private lateinit var productsList: EditText
    private lateinit var ilosc: EditText
    private lateinit var addButton: ImageButton
    private lateinit var dialogButton: Button

    private lateinit var skladnikiArr: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posilki)

        dialogButton = findViewById(R.id.dialog_btn)
        dialogButton.setOnClickListener{ showCustomDialog() }

        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)

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

    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)

        builder.setTitle("Custom Dialog")
            .setMessage("This is a custom dialog.")
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, which ->
                // User clicked OK button
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // User clicked Cancel button
            }
            .create()
            .show()
    }
}
