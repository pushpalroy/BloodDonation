package com.example.blooddonation.ui.admin

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AdminViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _camps = mutableStateListOf<BloodCamp>()
    val camps: List<BloodCamp> get() = _camps

    init {
        fetchCamps()
    }

    private fun fetchCamps() {
        db.collection("blood_camps")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    _camps.clear()
                    for (doc in it.documents) {
                        val camp = doc.toObject(BloodCamp::class.java)
                        camp?.let { c -> _camps.add(c.copy(id = doc.id)) }
                    }
                }
            }
    }

    fun addCamp(camp: BloodCamp) {
        db.collection("blood_camps")
            .add(camp)
    }

    fun updateCamp(camp: BloodCamp) {
        db.collection("blood_camps").document(camp.id)
            .set(camp)
    }

    fun deleteCamp(campId: String) {
        db.collection("blood_camps").document(campId)
            .delete()
    }
}
