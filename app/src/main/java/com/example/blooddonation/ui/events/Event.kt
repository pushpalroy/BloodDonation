package com.example.blooddonation.ui.events

import BloodCampViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BloodCampListScreen(viewModel: BloodCampViewModel = viewModel()) {
    val camps by viewModel.camps.collectAsState()
    val registeredCampIds by viewModel.registeredCampIds.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(camps) { camp ->
            BloodCampItem(
                camp = camp,
                isRegistered = registeredCampIds.contains(camp.id),
                onRegisterClick = { viewModel.registerForCamp(camp.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BloodCampItem(
    camp: BloodCamp,
    isRegistered: Boolean,
    onRegisterClick: () -> Unit
) {
    val redColor = Color(0xFFD32F2F)
    val blackColor = Color.Black
    val whiteColor = Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = whiteColor),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (camp.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = camp.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = camp.name, style = MaterialTheme.typography.titleLarge, color = redColor)
            Text(text = "Location: ${camp.location}", color = blackColor)
            Text(text = "Date: ${camp.date}", color = blackColor)
            Text(text = camp.description, color = blackColor)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) Color.Gray else redColor
                ),
                enabled = !isRegistered
            ) {
                Text(
                    text = if (isRegistered) "Registered" else "Register",
                    color = whiteColor
                )
            }
        }
    }
}



