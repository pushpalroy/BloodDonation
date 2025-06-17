package com.example.blooddonation.feature.requestblood

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blooddonation.domain.BloodRequest
import com.example.blooddonation.feature.chat.generateChatId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodRequestViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    // UI state
    private val _selectedBloodGroup = MutableStateFlow("")
    val selectedBloodGroup: StateFlow<String> = _selectedBloodGroup

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userName = MutableStateFlow("Anonymous")
    val userName: StateFlow<String> = _userName

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _requests = MutableStateFlow<List<BloodRequest>>(emptyList())
    val requests: StateFlow<List<BloodRequest>> = _requests

    init {
        listenToRequests()
    }

    fun onBloodGroupSelected(group: String) {
        _selectedBloodGroup.value = group
    }

    fun onLocationChanged(newLocation: String) {
        _location.value = newLocation
    }


    fun fetchUserName(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                _userName.value = doc.getString("username") ?: "Anonymous"
            }
            .addOnFailureListener {
                _userName.value = "Anonymous"
            }
    }

    fun addRequest(currentUserId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val group = _selectedBloodGroup.value
        val loc = _location.value
        val name = _userName.value
        if (group.isBlank() || loc.isBlank()) {
            _error.value = "Please select blood group and location"
            onFailure()
            return
        }

        _isLoading.value = true
        val newRequest = BloodRequest(
            requesterId = currentUserId,
            bloodGroup = group,
            requesterName = name,
            timestamp = System.currentTimeMillis(),
            location = loc,
            status = "pending"
        )

        db.collection("bloodRequests")
            .add(newRequest)
            .addOnSuccessListener {
                _isLoading.value = false
                _selectedBloodGroup.value = ""
                _location.value = ""
                onSuccess()
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value = "Failed to add request. Try again."
                onFailure()
            }
    }

    private fun listenToRequests() {
        // Remove previous listener if already set
        listenerRegistration?.remove()
        listenerRegistration = db.collection("bloodRequests")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("BloodRequestViewModel", "Listen failed.", e)
                    _error.value = "Failed to load requests: ${e.localizedMessage}"
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(BloodRequest::class.java)?.copy(id = it.id)
                } ?: emptyList()
                _requests.value = list
                _error.value = null // Clear error on successful update
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun addRequest(request: BloodRequest, onComplete: (Boolean) -> Unit) {
        // Optional: Prevent multiple pending requests by the same user
        val existingPending = _requests.value.any {
            it.requesterId == request.requesterId && it.status == "pending"
        }
        if (existingPending) {
            onComplete(false)
            _error.value = "You already have a pending request."
            return
        }

        db.collection("bloodRequests")
            .add(request)
            .addOnSuccessListener {
                // No need to reload; listener will update automatically
                onComplete(true)
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to add request: ${e.localizedMessage}"
                onComplete(false)
            }
    }

    fun rejectRequest(requestId: String) {
        db.collection("bloodRequests").document(requestId)
            .update("status", "rejected")
            .addOnFailureListener { e ->
                _error.value = "Failed to reject request: ${e.localizedMessage}"
                Log.e("BloodRequestViewModel", "Failed to reject request", e)
            }
    }

    fun getAcceptedRequestForDonor(donorId: String): BloodRequest? {
        return _requests.value.find {
            it.acceptedId == donorId && it.status == "accepted"
        }
    }

    fun acceptRequest(
        requestId: String,
        donorId: String,
        medicalInfo: String,
        onChatReady: (chatId: String, requesterId: String) -> Unit
    ) {
        db.collection("bloodRequests").document(requestId).get()
            .addOnSuccessListener { doc ->
                val requesterId = doc.getString("requesterId") ?: return@addOnSuccessListener
                val chatId = generateChatId(donorId, requesterId)

                // 1️⃣ store donor’s medical info (optional)
                db.collection("acceptances").add(
                    com.example.blooddonation.domain.Acceptance(requestId, donorId, medicalInfo)
                )

                // 2️⃣ ensure chat document exists
                val chatData = mapOf(
                    "donorId" to donorId,
                    "requesterId" to requesterId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("chats").document(chatId).set(chatData, SetOptions.merge())

                // 3️⃣ update the blood-request with every piece the requester will need
                val updateMap = mapOf(
                    "status" to "accepted",
                    "acceptedBy" to donorId,
                    "chatId" to chatId          // 🔹 the key field
                )
                db.collection("bloodRequests").document(requestId)
                    .update(updateMap)
                    .addOnSuccessListener {
                        // Listener will update requests automatically
                        onChatReady(chatId, requesterId)   // donor navigates now
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Failed to accept request: ${e.localizedMessage}"
                        Log.e("BloodRequestViewModel", "Error in acceptRequest", e)
                    }
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to fetch request: ${e.localizedMessage}"
                Log.e("BloodRequestViewModel", "Error in acceptRequest", e)
            }
    }
}
