package com.example.feature.auth

import androidx.lifecycle.ViewModel
import com.example.core.common.AuthState
import com.example.core.common.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Splash)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun completeSplash() {
        _authState.value = AuthState.Onboarding
    }

    fun completeOnboarding() {
        _authState.value = AuthState.Login
    }

    fun loginUser(email: String, name: String) {
        _currentUser.value = User(name = name, email = email)
        _authState.value = AuthState.LoggedIn
    }

    fun logoutUser() {
        _currentUser.value = null
        _authState.value = AuthState.Login
    }
}
