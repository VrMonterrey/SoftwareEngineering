package com.example.softwareengineering.model

data class Comment(
    val id: String? = "",
    val text: String = "",
    val ocena: Int = 0,
    val posilek: Posilki
)