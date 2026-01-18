package com.example.ayuda_v2.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.ayuda_v2.ui.screens.HelpModel

object ServicesRepository {
    private const val PREFS_NAME = "services_prefs"
    private const val KEY_SERVICES = "services"

    fun getAll(context: Context): List<HelpModel> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SERVICES, null) ?: return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<HelpModel>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val id = obj.optString("id", "${i}")
            val title = obj.optString("title", "")
            val subtitle = obj.optString("subtitle", "")
            list.add(HelpModel(id = id, title = title, subtitle = subtitle))
        }
        return list
    }

    fun saveAll(context: Context, services: List<HelpModel>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray()
        services.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("title", s.title)
            obj.put("subtitle", s.subtitle)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_SERVICES, arr.toString()).apply()
    }

    fun add(context: Context, service: HelpModel) {
        val list = getAll(context).toMutableList()
        list.add(service)
        saveAll(context, list)
    }

    fun update(context: Context, service: HelpModel) {
        val list = getAll(context).toMutableList()
        val idx = list.indexOfFirst { it.id == service.id }
        if (idx >= 0) {
            list[idx] = service
        }
        saveAll(context, list)
    }

    fun delete(context: Context, id: String) {
        val list = getAll(context).toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) list.removeAt(idx)
        saveAll(context, list)
    }
}
