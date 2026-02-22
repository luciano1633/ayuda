package com.example.ayuda_v2

import android.app.Application
import android.util.Log
import com.example.ayuda_v2.data.BookingRepository
import com.example.ayuda_v2.data.local.AppDatabase

/**
 * Custom Application class for app-wide initialization.
 *
 * Responsabilidades:
 * - Inicializar Room Database (singleton)
 * - Proveer BookingRepository (inyección manual de dependencias)
 * - Monitorear eventos de memoria
 *
 * Memory Leak Prevention:
 * - LeakCanary se inicializa automáticamente en builds de debug
 * - Uso de applicationContext en toda la app previene leaks de Activity/Fragment
 */
class AyudaApplication : Application() {
    companion object {
        private const val TAG = "AyudaApplication"
    }

    /** Base de datos Room - singleton */
    val database: AppDatabase by lazy {
        Log.d(TAG, "Initializing Room database")
        AppDatabase.getInstance(this)
    }

    /** Repositorio de reservas - singleton */
    val bookingRepository: BookingRepository by lazy {
        Log.d(TAG, "Initializing BookingRepository")
        BookingRepository(database.bookingDao())
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application created - LeakCanary active in debug builds")
        Log.i(TAG, "App initialized successfully with Room database")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "System is running low on memory - consider releasing resources")
    }

    @Suppress("DEPRECATION")
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_RUNNING_LOW -> Log.w(TAG, "Memory trim: Running low")
            TRIM_MEMORY_RUNNING_CRITICAL -> Log.w(TAG, "Memory trim: Running critical")
            TRIM_MEMORY_UI_HIDDEN -> Log.d(TAG, "Memory trim: UI hidden")
            else -> Log.d(TAG, "Memory trim level: $level")
        }
    }
}
