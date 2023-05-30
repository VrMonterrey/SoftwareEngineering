package com.example.softwareengineering.ui.theme

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ir.mahozad.android.PieChart

@Composable

fun PieChartView2(){
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                slices = listOf(
                    PieChart.Slice(0.35f, Color.parseColor("#FFC773"), legend = "Legend A", label = "Label A"),
                    PieChart.Slice(0.45f, Color.parseColor("#33F07F"), legend = "Legend A", label = "Label A"),
                    PieChart.Slice(0.2f, Color.parseColor("#FD6F6F"), legend = "Legend A", label = "Label A")
                )
                startAngle = -90
                isCenterLabelEnabled = true
                centerLabel = "Targeted\n97%"
                centerLabelColor = Color.GRAY
                centerLabelIconTint = Color.rgb(159, 181, 114)
                legendsTitleColor = Color.MAGENTA
                legendsAlignment = ir.mahozad.android.component.Alignment.START
                centerLabelIcon = PieChart.DefaultIcons.SQUARE_HOLLOW
//                legendTitleMargin = 14.dp
//                legendsTitleSize = 16.sp
                labelIconsTint = Color.BLACK
                labelType = PieChart.LabelType.OUTSIDE
                isLegendsPercentageEnabled = false
                labelIconsPlacement = PieChart.IconPlacement.TOP
                gradientType = PieChart.GradientType.SWEEP
                holeRatio = 0.85f
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary
        }
    )
}
