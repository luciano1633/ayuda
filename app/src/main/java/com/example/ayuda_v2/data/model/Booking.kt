package com.example.ayuda_v2.data.model

/**
 * Representa una reserva realizada por el usuario.
 * Contiene la información del servicio seleccionado y los datos del usuario.
 */
data class Booking(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val serviceIcon: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val scheduledDate: String,  // Formato: "2026-02-20"
    val scheduledTime: String,  // Formato: "10:00"
    val notes: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Estados posibles de una reserva.
 */
enum class BookingStatus(val displayName: String) {
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada")
}

