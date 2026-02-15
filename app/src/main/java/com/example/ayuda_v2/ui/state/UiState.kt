package com.example.ayuda_v2.ui.state

/**
 * Sealed class representing different UI states for async operations.
 * This helps manage loading, success, and error states in a type-safe manner.
 */
sealed class UiState<out T> {
    /**
     * Initial idle state before any operation starts.
     */
    object Idle : UiState<Nothing>()

    /**
     * Loading state while an async operation is in progress.
     */
    object Loading : UiState<Nothing>()

    /**
     * Success state containing the result data.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state containing the error message and optional exception.
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : UiState<Nothing>()
}

