# 🎬 Videos Demostrativos

Esta carpeta contiene los videos que documentan las mejoras técnicas implementadas en la aplicación.

---

## ✅ Videos Disponibles

| Archivo | Descripción | Contenido |
|---------|-------------|-----------|
| `leaks.mp4` | LeakCanary | Demostración de la app Leaks mostrando que no hay memory leaks |
| `logcat.mp4` | Logcat Debugging | Uso de filtros por TAG en Android Studio para ver logs |
| `Memory.mp4` | Android Profiler | Monitoreo de uso de memoria durante la ejecución |

---

## 📋 Descripción de Cada Video

### 1. leaks.mp4 - LeakCanary
Muestra:
- App "Leaks" (canario amarillo) instalada automáticamente
- Navegación por la aplicación para provocar posibles leaks
- Resultado: "No leaks detected" ✅

### 2. logcat.mp4 - Debugging con Logcat
Muestra:
- Filtrado por TAGs: `BookingRepository`, `BookingViewModel`
- Logs de operaciones CRUD (crear, leer, actualizar, eliminar reservas)
- Niveles de log: Debug (D), Warning (W), Error (E)

### 3. Memory.mp4 - Android Profiler
Muestra:
- Uso del Memory Profiler en Android Studio
- Gráfico de uso de memoria durante operaciones
- Verificación de que no hay memory leaks significativos

---

## 🔗 Referencias en el Código

Los videos documentan las siguientes implementaciones:

```kotlin
// BookingViewModel.kt - Logging
Log.d(TAG, "Loading bookings")
Log.e(TAG, "Error creating booking", e)

// BookingRepository.kt - Operaciones asíncronas
suspend fun getAll(context: Context): List<Booking> = withContext(Dispatchers.IO) { ... }

// AyudaApplication.kt - Monitoreo de memoria
override fun onTrimMemory(level: Int) { ... }
```

---

**Fecha**: Febrero 2026
