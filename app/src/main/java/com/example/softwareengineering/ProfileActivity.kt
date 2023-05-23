package com.example.softwareengineering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var switchNotifications: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Field init.
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextEmail = findViewById(R.id.editTextEmail)
        spinnerGender = findViewById(R.id.spinnerGender)
        switchNotifications = findViewById(R.id.switchNotifications)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
//            editTextFirstName.setText(currentUser.firstName)
//            editTextLastName.setText(currentUser.lastName)
            editTextEmail.setText(currentUser.email)
//            spinnerGender.setSelection(getGenderIndex(currentUser.gender))
//            switchNotifications.isChecked = currentUser.notificationsEnabled
        }

        val genderOptions = arrayOf("Męska", "Żeńska")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        //On submit btn click
        val buttonSave: ImageButton = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveProfile()
        }

        //Navigation
        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)

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
    }

    private fun getGenderIndex(gender: String): Int {
        return if (gender == "Żeńska ") 1 else 0
    }

    private fun saveProfile() {
        val firstName = editTextFirstName.text.toString()
        val lastName = editTextLastName.text.toString()
        val email = editTextEmail.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val notificationsEnabled = switchNotifications.isChecked

        // add save to db functionality

        Toast.makeText(this, "Profil został zapisany", Toast.LENGTH_SHORT).show()
    }

}