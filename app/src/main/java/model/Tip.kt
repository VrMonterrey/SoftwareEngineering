package com.example.softwareengineering.model

data class Tip(
    val id: String? = "",
    val topic: String = "",
    val text: String = "",
    val userId: String? = "",
    val userPhotoUrl: String? = "",
    val date: Long = System.currentTimeMillis(),
)