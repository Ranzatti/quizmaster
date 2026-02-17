package com.example.quizandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.quizandroid.ui.login.HomeScreen
import com.example.quizandroid.ui.login.LoginScreen
import com.example.quizandroid.ui.login.RegisterScreen
import com.example.quizandroid.ui.theme.QuizAndroidTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAndroidTheme {
                // Verifica se já existe um usuário logado no Firebase
                var currentScreen by remember {
                    mutableStateOf(if (FirebaseAuth.getInstance().currentUser != null) "home" else "login")
                }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" }
                    )
                    "register" -> RegisterScreen(
                        onRegisterSuccess = { currentScreen = "home" },
                        onNavigateBack = { currentScreen = "login" }
                    )
                    "home" -> HomeScreen(
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            currentScreen = "login"
                        }
                    )
                }
            }
        }
    }
}