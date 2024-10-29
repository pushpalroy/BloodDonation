package com.example.blooddonation.ui.registration
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    private val _registrationError = MutableLiveData<String?>()
    val registrationError: LiveData<String?> get() = _registrationError

    fun registerUser(user: User, password: String) {
        _isLoading.value = true

        // Trim and log email immediately
        val trimmedEmail = user.email.trim()
        Log.d(
            "UserViewModel",
            "Trimmed Email entered: $trimmedEmail"
        ) // Log email input for debugging


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    userId?.let {
                        FirebaseFirestore.getInstance().collection("users").document(it)
                            .set(user)
                            .addOnSuccessListener {
                                _isLoading.value = false
                                _isRegistered.value = true
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                _registrationError.value = e.localizedMessage
                            }
                    }
                } else {
                    _isLoading.value = false
                    _registrationError.value = task.exception?.localizedMessage
                }
            }
    }}



