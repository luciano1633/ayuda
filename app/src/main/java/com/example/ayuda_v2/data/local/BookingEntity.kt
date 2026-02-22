package com.example.ayuda_v2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus

/**
 * Entidad Room para persistir reservas en la base de datos local.
 * Mapea 1:1 con el modelo de dominio [Booking].
 */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val serviceIcon: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val notes: String = "",
    val status: String = BookingStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Convierte la entidad Room al modelo de dominio.
     */
    fun toBooking(): Booking = Booking(
        id = id,
        serviceId = serviceId,
        serviceName = serviceName,
        serviceIcon = serviceIcon,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        scheduledDate = scheduledDate,
        scheduledTime = scheduledTime,
        notes = notes,
        status = try { BookingStatus.valueOf(status) } catch (_: Exception) { BookingStatus.PENDING },
        createdAt = createdAt
    )
}

/**
 * Extensión para convertir un modelo de dominio a entidad Room.
 */
fun Booking.toEntity(): BookingEntity = BookingEntity(
    id = id,
    serviceId = serviceId,
    serviceName = serviceName,
    serviceIcon = serviceIcon,
    customerName = customerName,
    customerPhone = customerPhone,
    customerAddress = customerAddress,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime,
    notes = notes,
    status = status.name,
    createdAt = createdAt
)

