package com.example.core.common

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String, val exception: Throwable? = null) : UiState<Nothing>
    object Empty : UiState<Nothing>
}
