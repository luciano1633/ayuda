package com.example.ayuda_v2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room para la aplicación.
 * Gestiona la persistencia local de reservas.
 *
 * Beneficios sobre SharedPreferences:
 * - Consultas SQL optimizadas
 * - Verificación en tiempo de compilación
 * - Soporte nativo de Flow/Coroutines
 * - Migraciones de esquema
 */
@Database(entities = [BookingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia singleton de la base de datos.
         * Thread-safe con double-checked locking.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ayuda_v2_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

