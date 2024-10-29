package com.example.blooddonation.ui.registration

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UserRepository {
    private val db = Firebase.firestore

    fun registerUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(User::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }
}
