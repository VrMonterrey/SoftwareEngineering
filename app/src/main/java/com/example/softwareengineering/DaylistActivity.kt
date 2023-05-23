package com.example.softwareengineering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.View
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
class DaylistActivity : AppCompatActivity() {
    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var categories: ImageButton
//    private lateinit var profile: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daylist)


        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
//        profile = findViewById(R.id.profile_button)

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

//        profile.setOnClickListener(View.OnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            var intent: Intent = Intent(applicationContext, Profile::class.java)
//            startActivity(intent)
//            finish()
//        })
    }
}