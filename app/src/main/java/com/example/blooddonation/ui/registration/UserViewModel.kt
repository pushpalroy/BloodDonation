package com.example.blooddonation.ui.registration
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    private val _registrationError = MutableStateFlow("")
    val registrationError: StateFlow<String> = _registrationError.asStateFlow()

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
                                _registrationError.value = e.localizedMessage?.toString() ?: ""
                            }
                    }
                } else {
                    _isLoading.value = false
                    _registrationError.value = task.exception?.localizedMessage.toString()
                }
            }
    }}



