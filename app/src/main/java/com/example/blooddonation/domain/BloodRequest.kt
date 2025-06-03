package com.example.blooddonation.domain



data class BloodRequest(
    val id: String = "",
    val requesterId: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val status: String = "pending"
)

data class Acceptance(
    val id: String = "",
    val requestId: String = "",
    val donorId: String = "",
    val medicalInfo: String = ""
)


