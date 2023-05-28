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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
    suspend fun fetchUserEatenEntries(userId: String): List<Eaten> = suspendCoroutine { cont ->
        val eatenRef = FirebaseDatabase.getInstance().getReference("eaten")
        eatenRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eatenEntries = dataSnapshot.children.mapNotNull { it.getValue(Eaten::class.java) }
                cont.resume(eatenEntries)
            }
            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(error.toException())
            }
        })
    }
    fun aggregateEatenEntriesByDay(eatenEntries: List<Eaten>): Map<String, Macros> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val aggregatedEntries = mutableMapOf<String, Macros>()

        for (entry in eatenEntries) {
            val date = format.format(Date(entry.date))
            val existingMacros = aggregatedEntries[date]

            if (existingMacros == null) {
                aggregatedEntries[date] = Macros(
                    cals = entry.calories,
                    prots = entry.protein,
                    carbs = entry.carbs,
                    fats = entry.fat,
                    date = entry.date
                )
            } else {
                existingMacros.cals += entry.calories
                existingMacros.prots += entry.protein
                existingMacros.carbs += entry.carbs
                existingMacros.fats += entry.fat
            }
        }
        return aggregatedEntries
    }
    fun updateBarChart(aggregatedMacros: Map<String, Macros>) {
        val barChart = findViewById<BarChart>(R.id.idBarChart)

        // Create BarEntry for each day
        val caloriesEntries = aggregatedMacros.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.cals.toFloat())
        }
        val proteinsEntries = aggregatedMacros.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.prots.toFloat())
        }
        val carbsEntries = aggregatedMacros.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.carbs.toFloat())
        }
        val fatsEntries = aggregatedMacros.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.fats.toFloat())
        }

        // Create BarDataSets
        val caloriesDataSet = BarDataSet(caloriesEntries, "Calories")
        caloriesDataSet.color = Color.rgb(104, 241, 175)
        caloriesDataSet.valueTextColor = Color.rgb(104, 241, 175)

        val proteinsDataSet = BarDataSet(proteinsEntries, "Proteins")
        proteinsDataSet.color = Color.rgb(164, 228, 251)
        proteinsDataSet.valueTextColor = Color.rgb(164, 228, 251)

        val carbsDataSet = BarDataSet(carbsEntries, "Carbs")
        carbsDataSet.color = Color.rgb(242, 247, 158)
        carbsDataSet.valueTextColor = Color.rgb(242, 247, 158)

        val fatsDataSet = BarDataSet(fatsEntries, "Fats")
        fatsDataSet.color = Color.rgb(255, 102, 0)
        fatsDataSet.valueTextColor = Color.rgb(255, 102, 0)

        // Create BarData and set it to BarChart
        val data = BarData(caloriesDataSet, proteinsDataSet, carbsDataSet, fatsDataSet)
        barChart.data = data
        barChart.isDragEnabled = true
        barChart.setScaleEnabled(true)
        barChart.xAxis.axisMinimum = 0f
        barChart.xAxis.axisMaximum = data.entryCount.toFloat()
        // Set x-axis labels to the dates
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(aggregatedMacros.keys.toList())

        val yAxisLeft = barChart.axisLeft
        val yAxisRight = barChart.axisRight

        yAxisLeft.axisMinimum = 0f
        yAxisRight.axisMinimum = 0f

        val textColor = Color.WHITE

        // For X-Axis Labels
        barChart.xAxis.textColor = textColor

        // For Y-Axis Labels (Left and Right)
        barChart.axisLeft.textColor = textColor
        barChart.axisRight.textColor = textColor

        // For Legend Labels
        barChart.legend.textColor = textColor


        // Remove description label
        barChart.description.isEnabled = false

        // Add space between bars
        val groupSpace = 0.08f
        val barSpace = 0.02f
        val barWidth = 0.2f
        data.barWidth = barWidth
        barChart.groupBars(0f, groupSpace, barSpace)

        // Update the BarChart
        barChart.invalidate()
    }

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

        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid ?: ""
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val eatenEntries = fetchUserEatenEntries(currentUserId)
                val aggregatedMacros = aggregateEatenEntriesByDay(eatenEntries)

                withContext(Dispatchers.Main) {
                    updateBarChart(aggregatedMacros)
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }

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
