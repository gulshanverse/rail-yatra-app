package com.example.core.common

sealed interface AuthState {
    object Splash : AuthState
    object Onboarding : AuthState
    object Login : AuthState
    object Register : AuthState
    object ForgotPassword : AuthState
    object LoggedIn : AuthState
}

data class User(
    val name: String,
    val email: String
)
