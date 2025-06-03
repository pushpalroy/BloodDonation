package com.example.blooddonation.ui.profile

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val bio: String = "",
    val bloodGroup: String = "",
    val profileImageUrl: String = "",
    val isAvailable: Boolean = false
)

