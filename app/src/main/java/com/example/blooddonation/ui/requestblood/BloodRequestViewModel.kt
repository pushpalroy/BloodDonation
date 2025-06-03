package com.example.blooddonation.ui.requestblood

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blooddonation.domain.Acceptance
import com.example.blooddonation.domain.BloodRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodRequestViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _requests = MutableStateFlow<List<BloodRequest>>(emptyList())
    val requests: StateFlow<List<BloodRequest>> = _requests

    init {
        loadRequests()
    }

    fun addRequest(request: BloodRequest, onComplete: (Boolean) -> Unit) {
        db.collection("bloodRequests")
            .add(request)
            .addOnSuccessListener {
                loadRequests()
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun loadRequests() {
        db.collection("bloodRequests")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull {
                    it.toObject(BloodRequest::class.java)?.copy(id = it.id)
                }
                _requests.value = list
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Failed to load requests", e)
            }
    }

    fun rejectRequest(requestId: String) {
        db.collection("bloodRequests").document(requestId)
            .update("status", "rejected")
            .addOnSuccessListener {
                loadRequests()
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Failed to reject request", e)
            }
    }

    fun acceptRequest(requestId: String, donorId: String, medicalInfo: String) {
        val acceptance = Acceptance(
            requestId = requestId,
            donorId = donorId,
            medicalInfo = medicalInfo
        )

        db.collection("acceptances")
            .add(acceptance)
            .addOnSuccessListener {
                db.collection("bloodRequests").document(requestId)
                    .update("status", "accepted")
                    .addOnSuccessListener {
                        loadRequests()
                    }
                    .addOnFailureListener { e ->
                        Log.e("BloodRequestViewModel", "Failed to update request status", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Failed to add acceptance", e)
            }
    }
}

