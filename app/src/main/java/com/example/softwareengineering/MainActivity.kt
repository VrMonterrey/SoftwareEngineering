package com.example.softwareengineering
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.softwareengineering.model.Eaten
import com.example.softwareengineering.model.SkladPosilku
import com.example.softwareengineering.model.Skladnik
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import model.Macros
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var logout: ImageButton
    private lateinit var home: ImageButton
    private lateinit var profile: ImageButton
    private lateinit var categories: ImageButton

    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private var user: FirebaseUser? = null

//    suspend fun fetchEatenEntries(): List<Eaten> = suspendCoroutine { cont ->
//        val eatenRef = FirebaseDatabase.getInstance().getReference("eaten")
//        eatenRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val eatenEntries = dataSnapshot.children.mapNotNull { it.getValue(Eaten::class.java) }
//                cont.resume(eatenEntries)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                cont.resumeWithException(error.toException())
//            }
//        })
//    }
//
//    suspend fun fetchSkladPosilkuEntries(posilkiId: String): List<SkladPosilku> = suspendCoroutine { cont ->
//        val skladPosilkuRef = FirebaseDatabase.getInstance().getReference("composition")
//        skladPosilkuRef.orderByChild("posilkiId").equalTo(posilkiId).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val skladPosilkuEntries = dataSnapshot.children.mapNotNull { it.getValue(SkladPosilku::class.java) }
//                cont.resume(skladPosilkuEntries)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                cont.resumeWithException(error.toException())
//            }
//        })
//    }
//
//    suspend fun fetchSkladnik(skladnikId: String): Skladnik = suspendCoroutine { cont ->
//        val skladnikRef = FirebaseDatabase.getInstance().getReference("products").child(skladnikId)
//        skladnikRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val skladnik = dataSnapshot.getValue(Skladnik::class.java)
//                if (skladnik != null) cont.resume(skladnik) else cont.resumeWithException(IllegalStateException("Skladnik not found"))
//            }
//            override fun onCancelled(error: DatabaseError) {
//                cont.resumeWithException(error.toException())
//            }
//        })
//    }
//    suspend fun fetchMacros(): List<Macros> {
//        val macrosList = mutableListOf<Macros>()
//
//        // Fetch 'eaten' entries
//        val eatenEntries = fetchEatenEntries()
//
//        for (eatenEntry in eatenEntries) {
//            // Fetch 'SkladPosilku' entries
//            val skladPosilkuEntries = fetchSkladPosilkuEntries(eatenEntry.posilekId)
//
//            for (skladPosilku in skladPosilkuEntries) {
//                // Fetch 'Skladnik' entries
//                val skladnik = fetchSkladnik(skladPosilku.skladnikId)
//
//                // Calculate the macros and pass the eaten date
//                val macros = calculateMacros(skladnik, skladPosilku.amount, eatenEntry.date)
//                macrosList.add(macros)
//            }
//        }
//        return macrosList
//    }
//
//    fun calculateMacros(skladnik: Skladnik, amount: Int, date: Long): Macros {
//        val factor = amount / 100f
//        return Macros(
//            cals = skladnik.calories * factor,
//            prots = skladnik.protein * factor,
//            carbs = skladnik.carbs * factor,
//            fats = skladnik.fat * factor,
//            date = date
//        )
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    fun aggregateMacrosByDay(macrosList: List<Macros>): Map<String, Macros> {
//        val aggregatedMacros = mutableMapOf<String, Macros>()
//        val sdf = SimpleDateFormat("yyyy-MM-dd")
//
//        for (macros in macrosList) {
//            val date = sdf.format(Date(macros.date))
//            val aggregatedMacro = aggregatedMacros[date]
//            if (aggregatedMacro == null) {
//                aggregatedMacros[date] = macros
//            } else {
//                aggregatedMacro.cals += macros.cals
//                aggregatedMacro.prots += macros.prots
//                aggregatedMacro.carbs += macros.carbs
//                aggregatedMacro.fats += macros.fats
//            }
//        }
//        return aggregatedMacros
//    }
//
//    fun updateBarChart(aggregatedMacros: Map<String, Macros>) {
//        val barChart = findViewById<BarChart>(R.id.idBarChart)
//
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//        val entries = aggregatedMacros.map {
//            val dateMillis = sdf.parse(it.key).time.toFloat()
//            BarEntry(dateMillis, floatArrayOf(it.value.cals.toFloat(), it.value.prots.toFloat(), it.value.carbs.toFloat(), it.value.fats.toFloat()))
//        }
//
//        val colors = listOf(
//            Color.parseColor("#FF0000"), // Red
//            Color.parseColor("#00FF00"), // Green
//            Color.parseColor("#0000FF"), // Blue
//            Color.parseColor("#FFFF00")  // Yellow
//        )
//
//        val barDataSet = BarDataSet(entries, "Macros").apply {
//            setColors(colors)
//            stackLabels = arrayOf("Cals", "Prots", "Carbs", "Fats")
//        }
//
//        val barData = BarData(barDataSet)
//        barChart.data = barData
//
//        // Formatting X-Axis to display dates
//        val xAxis = barChart.xAxis
//        xAxis.valueFormatter = object : ValueFormatter() {
//            private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//            override fun getFormattedValue(value: Float): String {
//                return sdf.format(Date(value.toLong()))
//            }
//        }
//
//        barChart.invalidate() // refresh chart
//    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout = findViewById(R.id.logout_button)
        home = findViewById(R.id.home_button)
        categories = findViewById(R.id.categories_btn)
        profile = findViewById(R.id.profile_button)

        textView = findViewById(R.id.user)
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

//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val macrosList = fetchMacros()
//
//                // Aggregate macros by day
//                val aggregatedMacros = aggregateMacrosByDay(macrosList)
//
//                withContext(Dispatchers.Main) {
//                    // Update BarChart
//                    updateBarChart(aggregatedMacros)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        home.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        categories.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext,CategoriesActivity::class.java)
            startActivity(intent)
            finish()
        })

        profile.setOnClickListener(View.OnClickListener{
            var intent : Intent = Intent(applicationContext,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        })

        logout.setOnClickListener(View.OnClickListener{
            FirebaseAuth.getInstance().signOut()
            var intent : Intent = Intent(applicationContext,login::class.java)
            startActivity(intent)
            finish()
        })

    }

}
