package com.example.blooddonation.domain

data class AdminBloodCamp(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val date: String = "",
    val description: String = "",
    val imageUrl: String = "" // Firebase Storage download URL
)


