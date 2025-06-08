package com.example.blooddonation.feature.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(private val uid: String) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile
    private var listener: ListenerRegistration? = null

    init { fetchProfile() }

    private fun fetchProfile() {
        listener?.remove()
        listener = FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e("ProfileVM", "Firestore error", error)
                    _profile.value = null
                } else if (snap != null && snap.exists()) {
                    _profile.value = snap.toObject(UserProfile::class.java) ?: UserProfile()
                } else {
                    Log.w("ProfileVM", "No profile doc for $uid – using empty stub")
                    _profile.value = UserProfile()      // show blank but editable profile
                }
            }
    }

    fun updateProfile(updated: UserProfile, newImageUri: Uri?, ctx: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageUrl = newImageUri?.let { uri ->
                val storage = com.google.firebase.storage.ktx.storage
                val fileName = "profile_images/${'$'}uid_${'$'}{System.currentTimeMillis()}.jpg"
                val ref = storage.reference.child(fileName)
                ctx.contentResolver.openInputStream(uri)?.use { stream ->
                    ref.putStream(stream).await()
                }
                ref.downloadUrl.await().toString()
            } ?: updated.profileImagePath

            FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .set(updated.copy(profileImagePath = imageUrl))
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

