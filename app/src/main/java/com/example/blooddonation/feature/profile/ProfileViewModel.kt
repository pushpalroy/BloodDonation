package com.example.blooddonation.feature.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileViewModel(private val uid: String) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile
    private var listener: ListenerRegistration? = null

    init {
        fetchProfile()
    }

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
            // 1. Fix Storage bucket URL
            val storage = Firebase.storage("gs://registeractivity-202dd.firebasestorage.app")
            var imageUrl = updated.profileImagePath

            // 2. If there's a new image, upload and get download URL
            if (newImageUri != null) {
                try {
                    val fileName = "profile_images/${uid}_${System.currentTimeMillis()}.jpg"
                    val ref = storage.reference.child(fileName)
                    ctx.contentResolver.openInputStream(newImageUri)?.use { stream ->
                        val result = ref.putStream(stream).await()
                        if (result.task.isSuccessful) {
                            imageUrl = ref.downloadUrl.await().toString()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(ctx, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                    // continue to update text fields
                }
            }

            // 3. Update the Firestore profile (all fields)
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(updated.copy(profileImagePath = imageUrl))
                    .addOnSuccessListener {
                        // Must run Toast on main thread
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(ctx, "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(ctx, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

