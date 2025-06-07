package com.example.blooddonation.feature.viewdonors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.blooddonation.domain.BloodRequest
import com.example.blooddonation.feature.requestblood.BloodRequestViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blooddonation.R

@Composable
fun ViewDonorsScreen(
    navController: NavController,
    viewModel: BloodRequestViewModel = viewModel(),
    currentUserId: String
)
{
    val requests by viewModel.requests.collectAsState()
    var showMedicalFormForRequest by remember { mutableStateOf<BloodRequest?>(null) }

    // Filter out current user's own requests & keep only pending ones
    val filteredRequests = requests.filter {
        it.requesterId != currentUserId && it.status == "pending"
    }

    // Donor's accepted requests (where donor is current user)
    val donorsAcceptedRequests = requests.filter {
        it.acceptedBy == currentUserId && it.status == "accepted"
    }

    // Medical form dialog for accepting requests
    showMedicalFormForRequest?.let { request ->
        MedicalFormDialog(
            request = request,
            donorId = currentUserId,
            onDismiss = { showMedicalFormForRequest = null },
            onSubmit = { requestId, donorId, medicalInfo ->
                viewModel.acceptRequest(requestId, donorId, medicalInfo) { chatId, requesterId ->
                    showMedicalFormForRequest = null
                    // Navigate to ChatScreen with both user IDs
                    navController.navigate("chat/$chatId/$currentUserId/$requesterId")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.blood_background), // Replace with your drawable image name
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Your UI content on top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Optional: add semi-transparent overlay for readability
                .padding(16.dp)
        ) {
            // Show donor's accepted requests chat buttons
            if (donorsAcceptedRequests.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your Accepted Requests - Chats",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Text color to contrast background
                    )
                    Spacer(Modifier.height(8.dp))

                    donorsAcceptedRequests.forEach { acceptedRequest ->
                        val chatId = acceptedRequest.chatId ?: "" // assuming chatId stored in request
                        val requesterId = acceptedRequest.requesterId

                        if (chatId.isNotBlank()) {
                            Button(
                                onClick = {
                                    navController.navigate("chat/$chatId/$currentUserId/$requesterId")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Go to Chat with $requesterId", color = Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Show list of pending requests for donor to accept/reject
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRequests) { request ->
                    RequestCard(
                        bloodRequest = request,
                        onAccept = { showMedicalFormForRequest = request },
                        onReject = { viewModel.rejectRequest(request.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}





@Composable
fun RequestCard(
    bloodRequest: BloodRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Blood Group: ${bloodRequest.bloodGroup}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Location: ${bloodRequest.location}")
            Spacer(Modifier.height(8.dp))
            Row {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Accept") }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Reject") }
            }
        }
    }
}

@Composable
fun MedicalFormDialog(
    request: BloodRequest,
    donorId: String,
    onDismiss: () -> Unit,
    onSubmit: (requestId: String, donorId: String, medicalInfo: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            val scrollState = rememberScrollState()

            /* === form state === */
            var age by remember { mutableStateOf("") }
            var weight by remember { mutableStateOf("") }
            var hadIllness by remember { mutableStateOf("") }
            var medications by remember { mutableStateOf("") }
            var surgery by remember { mutableStateOf("") }
            var alcohol by remember { mutableStateOf("") }
            var chronicDiseases by remember { mutableStateOf("") }
            var exposedCovid by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    "Eligibility Form",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))

                /* ---------- numeric inputs ---------- */
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    label = { Text("Your age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter(Char::isDigit) },
                    label = { Text("Current weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                /* ---------- yes / no questions ---------- */
                YesNoDropdown(
                    label = "Had illness in the last 7 days?",
                    selected = hadIllness,
                    onSelect = { hadIllness = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Any medications you are taking?",
                    selected = medications,
                    onSelect = { medications = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Surgery in past 6 months?",
                    selected = surgery,
                    onSelect = { surgery = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Alcohol in last 24 hours?",
                    selected = alcohol,
                    onSelect = { alcohol = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Any chronic diseases?",
                    selected = chronicDiseases,
                    onSelect = { chronicDiseases = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Exposed to COVID-19 recently?",
                    selected = exposedCovid,
                    onSelect = { exposedCovid = it }
                )
                Spacer(Modifier.height(24.dp))

                /* ---------- buttons ---------- */
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        enabled = listOf(
                            age, weight, hadIllness, medications,
                            surgery, alcohol, chronicDiseases, exposedCovid
                        ).all { it.isNotBlank() },
                        onClick = {
                            // pack answers into a single string; adjust as you like
                            val info = buildString {
                                append("age=$age; ")
                                append("weight=$weight; ")
                                append("illness=$hadIllness; ")
                                append("medications=$medications; ")
                                append("surgery=$surgery; ")
                                append("alcohol=$alcohol; ")
                                append("chronicDiseases=$chronicDiseases; ")
                                append("exposedCovid=$exposedCovid")
                            }
                            onSubmit(request.id, donorId, info)
                        }
                    ) { Text("Submit") }
                }
            }
        }
    }
}

@Composable
private fun YesNoDropdown(
    label: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = { /* read-only */ },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("Yes", "No").forEach { answer ->
                DropdownMenuItem(
                    text = { Text(answer) },
                    onClick = {
                        onSelect(answer)
                        expanded = false
                    }
                )
            }
        }
    }
}
