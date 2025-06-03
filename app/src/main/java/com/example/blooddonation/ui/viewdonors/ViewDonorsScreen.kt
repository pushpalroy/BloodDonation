package com.example.blooddonation.ui.viewdonors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.blooddonation.domain.BloodRequest
import com.example.blooddonation.ui.requestblood.BloodRequestViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ViewDonorsScreen(
    navController: NavController,
    viewModel: BloodRequestViewModel = viewModel(),
    currentUserId: String
) {
    val requests by viewModel.requests.collectAsState()
    var showMedicalFormForRequest by remember { mutableStateOf<BloodRequest?>(null) }

    val filteredRequests = requests.filter { it.requesterId != currentUserId && it.status == "pending" }

    if (showMedicalFormForRequest != null) {
        MedicalFormDialog(
            request = showMedicalFormForRequest!!,
            donorId = currentUserId,
            onDismiss = { showMedicalFormForRequest = null },
            onSubmit = { requestId, donorId, medicalInfo ->
                viewModel.acceptRequest(requestId, donorId, medicalInfo)
                showMedicalFormForRequest = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(filteredRequests) { request ->
            RequestCard(
                bloodRequest = request,
                onAccept = { showMedicalFormForRequest = request },
                onReject = { viewModel.rejectRequest(request.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Blood Group: ${bloodRequest.bloodGroup}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Location: ${bloodRequest.location}")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                    Text("Accept")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Reject")
                }
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
    var medicalInfo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Medical Conditions Form") },
        text = {
            OutlinedTextField(
                value = medicalInfo,
                onValueChange = { medicalInfo = it },
                placeholder = { Text("Enter your medical conditions") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
        },
        confirmButton = {
            Button(onClick = {
                if (medicalInfo.isNotBlank()) {
                    onSubmit(request.id, donorId, medicalInfo)
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

