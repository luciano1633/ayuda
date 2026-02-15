package com.example.ayuda_v2.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.example.ayuda_v2.ui.screens.HelpModel

/**
 * Repository for managing services data persistence using SharedPreferences.
 * All operations are suspend functions for async execution on IO dispatcher.
 */
object ServicesRepository {
    private const val TAG = "ServicesRepository"
    private const val PREFS_NAME = "services_prefs"
    private const val KEY_SERVICES = "services"

    /**
     * Retrieves all services from SharedPreferences.
     * @param context Application context (use applicationContext to avoid leaks)
     * @return List of HelpModel objects
     */
    suspend fun getAll(context: Context): List<HelpModel> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting to load all services from SharedPreferences")
            val startTime = System.currentTimeMillis()

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_SERVICES, null)

            if (json.isNullOrEmpty()) {
                Log.d(TAG, "No services data found in SharedPreferences")
                return@withContext emptyList()
            }

            val arr = JSONArray(json)
            val list = mutableListOf<HelpModel>()

            for (i in 0 until arr.length()) {
                try {
                    val obj = arr.getJSONObject(i)
                    val id = obj.optString("id", "$i")
                    val title = obj.optString("title", "")
                    val subtitle = obj.optString("subtitle", "")
                    val imageUrl = obj.optString("imageUrl", null)
                    list.add(HelpModel(id = id, title = title, subtitle = subtitle, imageUrl = imageUrl))
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing service at index $i, skipping", e)
                    // Continue with next item instead of failing completely
                }
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "Successfully loaded ${list.size} services in ${elapsedTime}ms")
            list
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON data for services", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading services: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Saves all services to SharedPreferences.
     * @param context Application context (use applicationContext to avoid leaks)
     * @param services List of HelpModel objects to save
     */
    suspend fun saveAll(context: Context, services: List<HelpModel>) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting to save ${services.size} services to SharedPreferences")
            val startTime = System.currentTimeMillis()

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val arr = JSONArray()

            services.forEach { s ->
                try {
                    val obj = JSONObject().apply {
                        put("id", s.id)
                        put("title", s.title)
                        put("subtitle", s.subtitle)
                        s.imageUrl?.let { put("imageUrl", it) }
                    }
                    arr.put(obj)
                } catch (e: JSONException) {
                    Log.e(TAG, "Error creating JSON for service ${s.id}, skipping", e)
                }
            }

            val success = prefs.edit().putString(KEY_SERVICES, arr.toString()).commit()
            val elapsedTime = System.currentTimeMillis() - startTime

            if (success) {
                Log.d(TAG, "Successfully saved ${services.size} services in ${elapsedTime}ms")
            } else {
                Log.w(TAG, "SharedPreferences commit returned false, data may not be saved")
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error creating JSON data for services", e)
            throw e // Re-throw to be handled by caller
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error saving services: ${e.message}", e)
            throw e // Re-throw to be handled by caller
        }
    }

    /**
     * Clears all services data. Useful for testing or reset functionality.
     * @param context Application context
     */
    suspend fun clearAll(context: Context) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Clearing all services data")
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().remove(KEY_SERVICES).apply()
            Log.d(TAG, "Services data cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing services data", e)
        }
    }
}
