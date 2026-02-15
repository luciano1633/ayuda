package com.example.ayuda_v2

import android.app.Application
import android.util.Log

/**
 * Custom Application class for app-wide initialization.
 *
 * Memory Leak Prevention:
 * - LeakCanary is automatically initialized in debug builds (no manual setup needed)
 * - Using applicationContext throughout the app prevents Activity/Fragment context leaks
 *
 * LeakCanary will automatically:
 * - Monitor for memory leaks when Activities/Fragments are destroyed
 * - Show notifications when leaks are detected
 * - Provide detailed heap traces for debugging
 */
class AyudaApplication : Application() {
    companion object {
        private const val TAG = "AyudaApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application created - LeakCanary active in debug builds")

        // LeakCanary is automatically initialized in debug builds when the dependency is added
        // No manual configuration needed for basic usage

        // Log app startup for debugging
        Log.i(TAG, "App initialized successfully")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "System is running low on memory - consider releasing resources")
    }

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
