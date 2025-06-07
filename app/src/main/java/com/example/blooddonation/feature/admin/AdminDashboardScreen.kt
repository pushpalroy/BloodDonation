package com.example.blooddonation.feature.admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import coil.compose.rememberAsyncImagePainter
import com.example.blooddonation.domain.AdminBloodCamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private val RedColor = Color(0xFFD32F2F)
private val BlackColor = Color.Black
private val WhiteColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel()
) {

    val camps = viewModel.camps
    var searchQuery by remember { mutableStateOf("") }
    var sortAsc by remember { mutableStateOf(true) }

    /* ---------- derived list ---------- */
    val shownCamps = camps
        .filter { it.location.contains(searchQuery, ignoreCase = true) }
        .let { list ->
            if (sortAsc) list.sortedBy { it.date } else list.sortedByDescending { it.date }
        }

    /* ---------- add / edit dialog state ---------- */
    var showDialog by remember { mutableStateOf(false) }
    var selectedCamp by remember { mutableStateOf<AdminBloodCamp?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("signin") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedCamp = null
                showDialog = true
            }) { Icon(Icons.Default.Add, contentDescription = "Add Camp") }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            /* ----- search + sort row ----- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by location") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { sortAsc = !sortAsc }) {
                    Icon(
                        imageVector = if (sortAsc)
                            Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort by date"
                    )
                }
            }

            /* ----- list ----- */
            LazyColumn {
                items(shownCamps, key = { it.id }) { camp ->
                    CampItem(
                        camp = camp,
                        onEdit = {
                            selectedCamp = it
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteCamp(it.id) }
                    )
                }

                if (shownCamps.isEmpty()) {
                    item {
                        Text(
                            text = "No camps found for “$searchQuery”",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    /* ----- modal dialog ----- */
    if (showDialog) {
        CampDialog(
            initialCamp = selectedCamp,
            onDismiss = { showDialog = false },
            onSave = { camp ->
                if (camp.id.isEmpty()) viewModel.addCamp(camp)
                else viewModel.updateCamp(camp)
                showDialog = false
            }
        )
    }
}



@Composable
fun CampItem(camp: AdminBloodCamp, onEdit: (AdminBloodCamp) -> Unit, onDelete: (AdminBloodCamp) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Load and display local image
            if (camp.imageUrl.isNotEmpty()) {
                val imageFile = rememberSaveable { File(camp.imageUrl) }
                if (imageFile.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageFile),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Text(text = camp.name, style = MaterialTheme.typography.titleLarge, color = RedColor)
            Text(text = "Location: ${camp.location}", color = BlackColor)
            Text(text = "Date: ${camp.date}", color = BlackColor)
            Text(text = camp.description, color = BlackColor)

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = { onEdit(camp) },
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                ) {
                    Text("Edit", color = WhiteColor)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDelete(camp) },
                    colors = ButtonDefaults.buttonColors(containerColor = BlackColor)
                ) {
                    Text("Delete", color = WhiteColor)
                }
            }
        }
    }
}


@Composable
fun CampDialog(
    initialCamp: AdminBloodCamp?,
    onDismiss: () -> Unit,
    onSave: (AdminBloodCamp) -> Unit
) {

    // TODO: Add rememberSaveable

    var name by remember { mutableStateOf(initialCamp?.name ?: "") }
    var location by remember { mutableStateOf(initialCamp?.location ?: "") }
    var date by remember { mutableStateOf(initialCamp?.date ?: "") }
    var description by remember { mutableStateOf(initialCamp?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf(initialCamp?.imageUrl ?: "") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            scope.launch {
                val path = saveImageToInternalStorage(context, it)
                if (path != null) {
                    savedImagePath = path
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WhiteColor,
        title = {
            Text(
                text = if (initialCamp == null) "Add Camp" else "Edit Camp",
                color = RedColor
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Camp Name", color = BlackColor) },
                    textStyle = TextStyle(color = BlackColor)
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location", color = BlackColor) },
                    textStyle = TextStyle(color = BlackColor)
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date", color = BlackColor) },
                    textStyle = TextStyle(color = BlackColor)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = BlackColor) },
                    textStyle = TextStyle(color = BlackColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                ) {
                    Text("Choose Image", color = WhiteColor)
                }

                if (savedImagePath.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(File(savedImagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val camp = AdminBloodCamp(
                        id = initialCamp?.id ?: "",
                        name = name,
                        location = location,
                        date = date,
                        description = description,
                        imageUrl = savedImagePath
                    )
                    onSave(camp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedColor)
            ) {
                Text("Save", color = WhiteColor)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BlackColor)
            ) {
                Text("Cancel", color = WhiteColor)
            }
        }
    )
}


suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "camp_image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


