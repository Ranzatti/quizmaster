package com.example.quizandroid.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.quizandroid.R
import com.example.quizandroid.translateFirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Cor Laranja do Tema para combinar com o fundo
    val primaryOrange = Color(0xFFF57C00)

    val passwordsMatch = password == confirmPassword && password.isNotEmpty()
    val isFormValid = name.isNotEmpty() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 6 &&
            passwordsMatch

    // Padronização de cores laranja
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = primaryOrange,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = primaryOrange,
        unfocusedLabelColor = Color.Gray
    )

    val performRegister = {
        if (isFormValid) {
            isLoading = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userProfile = hashMapOf(
                            "name" to name, // Salvando conforme print do Firestore
                            "email" to email,
                            "score" to 0,
                            "quizzesDone" to 0,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        userId?.let { uid ->
                            db.collection("users").document(uid).set(userProfile)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Conta criada!", Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess()
                                }
                        }
                    } else {
                        isLoading = false
                        val mensagemAmigavel = translateFirebaseError(task.exception)
                        Toast.makeText(context, mensagemAmigavel, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // --- IMAGEM DE FUNDO ---
        Image(
            painter = painterResource(id = R.drawable.background_quiz),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // --- CARD COM ELEVAÇÃO (SOBRESALTO) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crie sua conta para jogar",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = primaryOrange
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Nome Único
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = primaryOrange) },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo E-mail
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = primaryOrange) },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = primaryOrange) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, null, tint = Color.Gray)
                        }
                    },
                    colors = fieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Confirmar Senha com Validação Visual
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = primaryOrange) },
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(image, null, tint = Color.Gray)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) primaryOrange else Color.Red,
                        unfocusedBorderColor = if (passwordsMatch || confirmPassword.isEmpty()) Color.LightGray else Color.Red,
                        focusedLabelColor = if (passwordsMatch || confirmPassword.isEmpty()) primaryOrange else Color.Red,
                        unfocusedLabelColor = Color.Gray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = primaryOrange)
                } else {
                    Button(
                        onClick = { performRegister() },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) primaryOrange else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("CRIAR CONTA", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onNavigateBack) {
                    Text("Voltar para o Login", color = primaryOrange)
                }
            }
        }
    }
}