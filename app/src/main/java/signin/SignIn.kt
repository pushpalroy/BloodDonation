package signin

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val CrimsonRed = Color(0xFFD32F2F)
    val PureWhite = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = PureBlack) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureBlack, RoundedCornerShape(8.dp)),
                    textStyle = TextStyle(color = PureBlack),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = PureBlack,
                        focusedBorderColor = CrimsonRed,
                        cursorColor = CrimsonRed
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = PureBlack) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureBlack, RoundedCornerShape(8.dp)),
                    textStyle = TextStyle(color = PureBlack),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = PureBlack,
                        focusedBorderColor = CrimsonRed,
                        cursorColor = CrimsonRed
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            error = null

                            val auth = FirebaseAuth.getInstance()
                            val firestore = FirebaseFirestore.getInstance()

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    val uid = result.user?.uid.orEmpty()
                                    firestore.collection("users").document(uid).get()
                                        .addOnSuccessListener { doc ->
                                            val name = doc.getString("name") ?: "User"
                                            val imageUri = doc.getString("imageUri") ?: "default"

                                            navController.navigate(
                                                "dashboard/${Uri.encode(name)}/${Uri.encode(imageUri)}/${Uri.encode(uid)}"
                                            )
                                        }
                                        .addOnFailureListener {
                                            error = "Failed to load profile: ${it.message}"
                                        }
                                        .addOnCompleteListener {
                                            isLoading = false
                                        }
                                }
                                .addOnFailureListener {
                                    error = "Authentication failed: ${it.message}"
                                    isLoading = false
                                }
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = PureWhite
                        )
                    } else {
                        Text("Sign In", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }

                error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = it, color = CrimsonRed, textAlign = TextAlign.Center)
                }
            }
        }
    }
}







