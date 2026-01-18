package com.example.ayuda_v2.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.ayuda_v2.data.ServicesRepository
import com.example.ayuda_v2.ui.screens.HelpModel
import java.util.UUID

class ServicesViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val _items = mutableStateListOf<HelpModel>()
    val items: List<HelpModel> get() = _items

    init {
        load()
    }

    fun load() {
        _items.clear()
        _items.addAll(ServicesRepository.getAll(ctx))
    }

    fun add(title: String, subtitle: String) {
        val id = UUID.randomUUID().toString()
        val item = HelpModel(id = id, title = title, subtitle = subtitle)
        ServicesRepository.add(ctx, item)
        _items.add(item)
    }

    fun update(id: String, title: String, subtitle: String) {
        val item = HelpModel(id = id, title = title, subtitle = subtitle)
        ServicesRepository.update(ctx, item)
        val idx = _items.indexOfFirst { it.id == id }
        if (idx >= 0) _items[idx] = item
    }

    fun delete(id: String) {
        ServicesRepository.delete(ctx, id)
        val idx = _items.indexOfFirst { it.id == id }
        if (idx >= 0) _items.removeAt(idx)
    }
}
