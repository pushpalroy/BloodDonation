import androidx.lifecycle.ViewModel
import com.example.blooddonation.domain.BloodCamp
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodCampViewModel : ViewModel() {

    private val _camps = MutableStateFlow<List<BloodCamp>>(emptyList())
    val camps: StateFlow<List<BloodCamp>> = _camps

    private val _registeredCampIds = MutableStateFlow<List<String>>(emptyList())
    val registeredCampIds: StateFlow<List<String>> = _registeredCampIds

    private val databaseReference = FirebaseDatabase.getInstance().getReference("bloodCamps")

    init {
        loadCamps()
    }

    private fun loadCamps() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val campList = mutableListOf<BloodCamp>()
                for (campSnapshot in snapshot.children) {
                    val camp = campSnapshot.getValue(BloodCamp::class.java)
                    camp?.let {
                        campList.add(it.copy(id = campSnapshot.key ?: ""))
                    }
                }
                _camps.value = campList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun registerForCamp(campId: String) {
        if (!_registeredCampIds.value.contains(campId)) {
            _registeredCampIds.value = _registeredCampIds.value + campId
        }
    }
}
