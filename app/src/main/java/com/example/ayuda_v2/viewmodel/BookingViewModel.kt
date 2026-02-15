package com.example.ayuda_v2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ayuda_v2.data.BookingRepository
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus
import com.example.ayuda_v2.data.model.PredefinedServices
import com.example.ayuda_v2.data.model.Service
import com.example.ayuda_v2.ui.state.UiState
import java.util.UUID

/**
 * ViewModel para gestionar servicios y reservas.
 * Implementa el patrón MVVM con procesamiento asincrónico.
 */
class BookingViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "BookingViewModel"
    }

    private val ctx = application.applicationContext

    // Lista de servicios predefinidos (no cambian)
    private val _services = MutableStateFlow<List<Service>>(PredefinedServices.services)
    val services: StateFlow<List<Service>> = _services

    // Lista de reservas del usuario
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    // Estado de la UI
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    // Manejador de excepciones
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Error in coroutine", throwable)
        _uiState.value = UiState.Error(
            message = throwable.message ?: "Error desconocido",
            exception = throwable
        )
    }

    init {
        loadBookings()
    }

    /**
     * Carga todas las reservas del usuario.
     */
    fun loadBookings() {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Loading bookings")

                val bookingsList = BookingRepository.getAll(ctx)
                _bookings.value = bookingsList

                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Loaded ${bookingsList.size} bookings")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bookings", e)
                _uiState.value = UiState.Error("Error al cargar reservas: ${e.message}", e)
            }
        }
    }

    /**
     * Obtiene un servicio por su ID.
     */
    fun getServiceById(serviceId: String): Service? {
        return PredefinedServices.getById(serviceId)
    }

    /**
     * Crea una nueva reserva.
     */
    fun createBooking(
        serviceId: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        scheduledDate: String,
        scheduledTime: String,
        notes: String = ""
    ) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Creating booking for service: $serviceId")

                val service = PredefinedServices.getById(serviceId)
                    ?: throw IllegalArgumentException("Servicio no encontrado")

                val booking = Booking(
                    id = UUID.randomUUID().toString(),
                    serviceId = serviceId,
                    serviceName = service.name,
                    serviceIcon = service.icon,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerAddress = customerAddress,
                    scheduledDate = scheduledDate,
                    scheduledTime = scheduledTime,
                    notes = notes,
                    status = BookingStatus.PENDING
                )

                withContext(Dispatchers.IO) {
                    BookingRepository.add(ctx, booking)
                }

                // Recargar lista
                _bookings.value = BookingRepository.getAll(ctx)

                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Booking created successfully: ${booking.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating booking", e)
                _uiState.value = UiState.Error("Error al crear reserva: ${e.message}", e)
            }
        }
    }

    /**
     * Cancela una reserva.
     */
    fun cancelBooking(bookingId: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Cancelling booking: $bookingId")

                withContext(Dispatchers.IO) {
                    BookingRepository.cancel(ctx, bookingId)
                }

                _bookings.value = BookingRepository.getAll(ctx)
                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Booking cancelled")
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling booking", e)
                _uiState.value = UiState.Error("Error al cancelar: ${e.message}", e)
            }
        }
    }

    /**
     * Marca una reserva como completada/realizada.
     */
    fun completeBooking(bookingId: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Completing booking: $bookingId")

                withContext(Dispatchers.IO) {
                    BookingRepository.updateStatus(ctx, bookingId, BookingStatus.COMPLETED)
                }

                _bookings.value = BookingRepository.getAll(ctx)
                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Booking marked as completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error completing booking", e)
                _uiState.value = UiState.Error("Error al completar: ${e.message}", e)
            }
        }
    }

    /**
     * Elimina una reserva.
     */
    fun deleteBooking(bookingId: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Deleting booking: $bookingId")

                withContext(Dispatchers.IO) {
                    BookingRepository.delete(ctx, bookingId)
                }

                _bookings.value = BookingRepository.getAll(ctx)
                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "Booking deleted")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting booking", e)
                _uiState.value = UiState.Error("Error al eliminar: ${e.message}", e)
            }
        }
    }

    /**
     * Resetea el estado de la UI.
     */
    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    /**
     * Simula un error para propósitos de debugging y testing.
     * Útil para verificar el manejo de errores y LeakCanary.
     */
    fun simulateError() {
        viewModelScope.launch(exceptionHandler) {
            Log.w(TAG, "Simulating error for debugging purposes")
            _uiState.value = UiState.Loading
            kotlinx.coroutines.delay(300)
            throw RuntimeException("Error simulado para testing - Esto es intencional")
        }
    }

    /**
     * Limpia todas las reservas (útil para testing).
     */
    fun clearAllBookings() {
        viewModelScope.launch(exceptionHandler) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "Clearing all bookings")

                withContext(Dispatchers.IO) {
                    val prefs = ctx.getSharedPreferences("bookings_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().clear().commit()
                }

                _bookings.value = emptyList()
                _uiState.value = UiState.Success(Unit)
                Log.d(TAG, "All bookings cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing bookings", e)
                _uiState.value = UiState.Error("Error al limpiar reservas: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared - resources released")
    }
}

