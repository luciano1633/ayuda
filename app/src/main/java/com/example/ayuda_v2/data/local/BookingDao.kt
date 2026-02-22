package com.example.ayuda_v2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones CRUD de reservas.
 * Utiliza Flow para emitir cambios reactivos a la UI.
 */
@Dao
interface BookingDao {

    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateStatus(bookingId: String, status: String)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteById(bookingId: String)

    @Query("DELETE FROM bookings")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getCount(): Int
}

