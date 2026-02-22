package com.example.ayuda_v2.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.example.ayuda_v2.data.local.BookingDao
import com.example.ayuda_v2.data.local.toEntity
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus

/**
 * Interfaz del repositorio de reservas.
 * Permite inyección de dependencias y facilita el testing con mocks.
 */
interface IBookingRepository {
    fun getAllBookingsFlow(): Flow<List<Booking>>
    suspend fun add(booking: Booking)
    suspend fun updateStatus(bookingId: String, status: BookingStatus)
    suspend fun cancel(bookingId: String)
    suspend fun delete(bookingId: String)
    suspend fun deleteAll()
    suspend fun getCount(): Int
}

/**
 * Repositorio para gestionar las reservas del usuario.
 * Utiliza Room (BookingDao) para persistencia local.
 *
 * Migrado desde SharedPreferences a Room para:
 * - Consultas SQL optimizadas
 * - Flow reactivo (actualizaciones automáticas de UI)
 * - Verificación en tiempo de compilación
 * - Mejor rendimiento en conjuntos de datos grandes
 */
class BookingRepository(private val bookingDao: BookingDao) : IBookingRepository {

    companion object {
        private const val TAG = "BookingRepository"
    }

    /**
     * Obtiene todas las reservas como Flow reactivo.
     * La UI se actualiza automáticamente cuando cambian los datos.
     */
    override fun getAllBookingsFlow(): Flow<List<Booking>> {
        Log.d(TAG, "Getting all bookings as Flow")
        return bookingDao.getAllBookings().map { entities ->
            entities.map { it.toBooking() }
        }
    }

    /**
     * Agrega una nueva reserva.
     */
    override suspend fun add(booking: Booking) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding booking: ${booking.id} for service: ${booking.serviceName}")
                val startTime = System.currentTimeMillis()
                bookingDao.insert(booking.toEntity())
                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "Booking added successfully in ${elapsed}ms")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding booking: ${booking.id}", e)
                throw e
            }
        }
    }

    /**
     * Actualiza el estado de una reserva.
     */
    override suspend fun updateStatus(bookingId: String, status: BookingStatus) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Updating booking $bookingId status to $status")
                bookingDao.updateStatus(bookingId, status.name)
                Log.d(TAG, "Booking $bookingId status updated to $status")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating booking status: $bookingId", e)
                throw e
            }
        }
    }

    /**
     * Cancela una reserva.
     */
    override suspend fun cancel(bookingId: String) {
        updateStatus(bookingId, BookingStatus.CANCELLED)
    }

    /**
     * Elimina una reserva.
     */
    override suspend fun delete(bookingId: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting booking: $bookingId")
                bookingDao.deleteById(bookingId)
                Log.d(TAG, "Booking deleted: $bookingId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting booking: $bookingId", e)
                throw e
            }
        }
    }

    /**
     * Elimina todas las reservas.
     */
    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting all bookings")
                bookingDao.deleteAll()
                Log.d(TAG, "All bookings deleted")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting all bookings", e)
                throw e
            }
        }
    }

    /**
     * Obtiene el conteo total de reservas.
     */
    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        try {
            bookingDao.getCount()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting booking count", e)
            0
        }
    }
}


