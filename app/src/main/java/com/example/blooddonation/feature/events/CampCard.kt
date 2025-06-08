package com.example.blooddonation.feature.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun CampCard(
    modifier: Modifier = Modifier,
    name: String,
    location: String,
    date: String,
    description: String,
    imagePath: String = "",
    actionContent: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (imagePath.isNotEmpty()) {
                val model = if (File(imagePath).exists()) File(imagePath) else imagePath
                Image(
                    painter = rememberAsyncImagePainter(model = model),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("Location: $location", color = MaterialTheme.colorScheme.onBackground)
            Text("Date: $date", color = MaterialTheme.colorScheme.onBackground)
            Text(description, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                content = actionContent
            )
        }
    }
}

