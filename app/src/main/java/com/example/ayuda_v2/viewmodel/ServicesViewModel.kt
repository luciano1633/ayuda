package com.example.ayuda_v2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ayuda_v2.data.ServicesRepository
import com.example.ayuda_v2.ui.screens.HelpModel
import com.example.ayuda_v2.ui.state.UiState
import java.util.UUID

/**
 * ViewModel for managing services data with proper async handling and memory management.
 * Implements MVVM pattern with StateFlow for reactive UI updates.
 */
class ServicesViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ServicesViewModel"
        private const val SIMULATED_DELAY_MS = 500L // Simulated network delay for testing
    }

    // Using applicationContext to prevent Activity context leaks
    private val ctx = application.applicationContext

    // StateFlow for items list
    private val _items = MutableStateFlow<List<HelpModel>>(emptyList())
    val items: StateFlow<List<HelpModel>> get() = _items

    // StateFlow for UI state (loading, success, error)
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> get() = _uiState

    // Coroutine exception handler for global error handling
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception caught", throwable)
        _uiState.value = UiState.Error(
            message = throwable.message ?: "Unknown error occurred",
            exception = throwable
        )
    }

    init {
        load()
    }

    /**
     * Loads all services from repository asynchronously.
     * Uses Dispatchers.IO for background processing.
     */
    fun load() {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Starting to load services in ViewModel")

                // Simulate network delay for testing async behavior
                withContext(Dispatchers.IO) {
                    delay(SIMULATED_DELAY_MS)
                }

                val services = ServicesRepository.getAll(ctx)
                _items.value = services
                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Services loaded successfully: ${services.size} items")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading services in ViewModel", e)
                _items.value = emptyList()
                _uiState.value = UiState.Error(
                    message = "Failed to load services: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Adds a new service asynchronously.
     * @param title Service title
     * @param subtitle Service subtitle
     * @param imageUrl Optional image URL for the service
     */
    fun add(title: String, subtitle: String, imageUrl: String? = null) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Adding service: $title")

                val id = UUID.randomUUID().toString()
                val item = HelpModel(id = id, title = title, subtitle = subtitle, imageUrl = imageUrl)
                val newList = _items.value.toMutableList().apply { add(item) }
                _items.value = newList

                // Save in background thread
                withContext(Dispatchers.IO) {
                    ServicesRepository.saveAll(ctx, newList)
                }

                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Service added successfully with id: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding service in ViewModel", e)
                _uiState.value = UiState.Error(
                    message = "Failed to add service: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Updates an existing service asynchronously.
     * @param id Service ID to update
     * @param title New title
     * @param subtitle New subtitle
     * @param imageUrl Optional new image URL
     */
    fun update(id: String, title: String, subtitle: String, imageUrl: String? = null) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Updating service with id: $id")

                val item = HelpModel(id = id, title = title, subtitle = subtitle, imageUrl = imageUrl)
                val newList = _items.value.toMutableList().apply {
                    val idx = indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        set(idx, item)
                        Log.d(TAG, "Service found at index $idx, updating")
                    } else {
                        Log.w(TAG, "Service with id $id not found for update")
                    }
                }
                _items.value = newList

                // Save in background thread
                withContext(Dispatchers.IO) {
                    ServicesRepository.saveAll(ctx, newList)
                }

                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Service updated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating service in ViewModel", e)
                _uiState.value = UiState.Error(
                    message = "Failed to update service: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Deletes a service by ID asynchronously.
     * @param id Service ID to delete
     */
    fun delete(id: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Deleting service with id: $id")

                val newList = _items.value.toMutableList().apply {
                    val idx = indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        removeAt(idx)
                        Log.d(TAG, "Service removed from index $idx")
                    } else {
                        Log.w(TAG, "Service with id $id not found for deletion")
                    }
                }
                _items.value = newList

                // Save in background thread
                withContext(Dispatchers.IO) {
                    ServicesRepository.saveAll(ctx, newList)
                }

                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Service deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting service in ViewModel", e)
                _uiState.value = UiState.Error(
                    message = "Failed to delete service: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Simulates an error for testing error handling and LeakCanary.
     * This method intentionally throws an exception to test debugging capabilities.
     */
    fun simulateError() {
        viewModelScope.launch(exceptionHandler) {
            Log.w(TAG, "Simulating error for debugging purposes")
            _uiState.value = UiState.Loading
            delay(300)
            throw RuntimeException("Simulated error for testing - This is intentional")
        }
    }

    /**
     * Resets the UI state to Idle.
     */
    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    /**
     * Called when ViewModel is cleared. Good practice to log for debugging.
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared - resources released")
    }
}
