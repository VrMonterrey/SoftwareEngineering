package com.example.softwareengineering.model

data class DailyNutrition(
    val id: String? = "",
    val date: Long = System.currentTimeMillis(),
    val userId: String = "",
    val posilekId: String = ""
)