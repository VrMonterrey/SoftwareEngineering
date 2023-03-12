package com.example.softwareengineering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private var user: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.logout)
        textView = findViewById(R.id.user_details)
        auth = Firebase.auth
        user = auth.currentUser
        if (user == null){
            var intent : Intent = Intent(applicationContext,login::class.java)
            startActivity(intent)
            finish()
        }
        else{
            textView.text=user?.email
        }

        button.setOnClickListener(View.OnClickListener{
            FirebaseAuth.getInstance().signOut()
            var intent : Intent = Intent(applicationContext,login::class.java)
            startActivity(intent)
            finish()
        })
    }
}