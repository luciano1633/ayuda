package com.example.ayuda_v2.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus

/**
 * Repository para gestionar las reservas del usuario.
 * Persiste los datos en SharedPreferences con formato JSON.
 */
object BookingRepository {
    private const val TAG = "BookingRepository"
    private const val PREFS_NAME = "bookings_prefs"
    private const val KEY_BOOKINGS = "bookings"

    /**
     * Obtiene todas las reservas del usuario.
     */
    suspend fun getAll(context: Context): List<Booking> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Loading all bookings from SharedPreferences")
            val startTime = System.currentTimeMillis()

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_BOOKINGS, null)

            if (json.isNullOrEmpty()) {
                Log.d(TAG, "No bookings found")
                return@withContext emptyList()
            }

            val arr = JSONArray(json)
            val list = mutableListOf<Booking>()

            for (i in 0 until arr.length()) {
                try {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        Booking(
                            id = obj.getString("id"),
                            serviceId = obj.getString("serviceId"),
                            serviceName = obj.getString("serviceName"),
                            serviceIcon = obj.optString("serviceIcon", "📋"),
                            customerName = obj.getString("customerName"),
                            customerPhone = obj.getString("customerPhone"),
                            customerAddress = obj.getString("customerAddress"),
                            scheduledDate = obj.getString("scheduledDate"),
                            scheduledTime = obj.getString("scheduledTime"),
                            notes = obj.optString("notes", ""),
                            status = BookingStatus.valueOf(obj.optString("status", "PENDING")),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing booking at index $i", e)
                }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "Loaded ${list.size} bookings in ${elapsedTime}ms")
            list.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bookings", e)
            emptyList()
        }
    }

    /**
     * Guarda todas las reservas.
     */
    suspend fun saveAll(context: Context, bookings: List<Booking>) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Saving ${bookings.size} bookings")
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val arr = JSONArray()

            bookings.forEach { booking ->
                val obj = JSONObject().apply {
                    put("id", booking.id)
                    put("serviceId", booking.serviceId)
                    put("serviceName", booking.serviceName)
                    put("serviceIcon", booking.serviceIcon)
                    put("customerName", booking.customerName)
                    put("customerPhone", booking.customerPhone)
                    put("customerAddress", booking.customerAddress)
                    put("scheduledDate", booking.scheduledDate)
                    put("scheduledTime", booking.scheduledTime)
                    put("notes", booking.notes)
                    put("status", booking.status.name)
                    put("createdAt", booking.createdAt)
                }
                arr.put(obj)
            }

            prefs.edit().putString(KEY_BOOKINGS, arr.toString()).commit()
            Log.d(TAG, "Bookings saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bookings", e)
            throw e
        }
    }

    /**
     * Agrega una nueva reserva.
     */
    suspend fun add(context: Context, booking: Booking) {
        val list = getAll(context).toMutableList()
        list.add(booking)
        saveAll(context, list)
        Log.d(TAG, "Booking added: ${booking.id}")
    }

    /**
     * Actualiza el estado de una reserva.
     */
    suspend fun updateStatus(context: Context, bookingId: String, status: BookingStatus) {
        val list = getAll(context).toMutableList()
        val idx = list.indexOfFirst { it.id == bookingId }
        if (idx >= 0) {
            list[idx] = list[idx].copy(status = status)
            saveAll(context, list)
            Log.d(TAG, "Booking $bookingId status updated to $status")
        }
    }

    /**
     * Cancela una reserva.
     */
    suspend fun cancel(context: Context, bookingId: String) {
        updateStatus(context, bookingId, BookingStatus.CANCELLED)
    }

    /**
     * Elimina una reserva.
     */
    suspend fun delete(context: Context, bookingId: String) {
        val list = getAll(context).toMutableList()
        list.removeIf { it.id == bookingId }
        saveAll(context, list)
        Log.d(TAG, "Booking deleted: $bookingId")
    }
}

