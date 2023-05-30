package com.example.softwareengineering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.softwareengineering.ui.theme.PieChartView
import com.example.softwareengineering.ui.theme.PieChartView2

class ChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center
//            ) {
//                // Preview with sample data
//                PieChartView(
//                    data = mapOf(
//                        Pair("Białko", 115),
//                        Pair("Węglewodany", 230),
//                        Pair("Tłuszcze", 80),
//                    )
//                )
//
//            }
            PieChartView2()
        }
    }
}