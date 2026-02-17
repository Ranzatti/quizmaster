package com.example.quizandroid

fun translateFirebaseError(exception: Exception?): String {
    return when (exception?.let { it::class.java.simpleName }) {
        "FirebaseAuthUserCollisionException" -> "Este e-mail já está cadastrado."
        "FirebaseAuthInvalidUserException" -> "Usuário não encontrado."
        "FirebaseAuthInvalidCredentialsException" -> "E-mail ou senha incorretos."
        "FirebaseAuthWeakPasswordException" -> "A senha deve ter pelo menos 6 caracteres."
        "FirebaseNetworkException" -> "Sem conexão com a internet."
        else -> exception?.localizedMessage ?: "Ocorreu um erro inesperado."
    }
}