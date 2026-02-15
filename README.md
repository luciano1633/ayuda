# 🆘 ¿Necesitas ayuda? - App de Servicios a Domicilio

Aplicación móvil Android desarrollada con Kotlin y Jetpack Compose que permite a los usuarios solicitar y agendar servicios a domicilio de manera rápida y sencilla.

---

## 🎬 Videos Demostrativos

La documentación visual se encuentra en la carpeta `docs/screenshots/`:

| Video | Descripción |
|-------|-------------|
| 📹 [leaks.mp4](docs/screenshots/leaks.mp4) | Demostración de LeakCanary - Detección de memory leaks |
| 📹 [logcat.mp4](docs/screenshots/logcat.mp4) | Uso de Logcat para debugging con TAGs |
| 📹 [Memory.mp4](docs/screenshots/Memory.mp4) | Android Profiler - Monitoreo de memoria |

---

## 🚀 Características Principales

- **12 servicios predefinidos** organizados por categorías:
  - 🏠 **Hogar**: Electricista, Gasfíter, Cerrajero, Pintor, Jardinero, Limpieza
  - 💻 **Tecnología**: Técnico PC, Técnico Celular
  - 🚗 **Vehículos**: Mecánico, Grúa
  - ❤️ **Salud**: Enfermera a Domicilio, Cuidador de Adulto Mayor

- **Sistema de reservas completo**:
  - Formulario con validación de datos
  - Selección de fecha y hora con DatePicker/TimePicker
  - Gestión de reservas (ver, completar, cancelar)

- **Arquitectura robusta**:
  - Patrón MVVM
  - Kotlin Coroutines para operaciones asíncronas
  - StateFlow para estados reactivos
  - Manejo de errores estructurado

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 2.0+ | Lenguaje principal |
| Jetpack Compose | Latest | UI declarativa |
| Kotlin Coroutines | 1.7.3 | Procesamiento asíncrono |
| Coil | 2.5.0 | Carga de imágenes |
| LeakCanary | 2.12 | Detección de memory leaks |
| Navigation Compose | 2.9.6 | Navegación entre pantallas |

---

## 📂 Estructura del Proyecto

```
app/src/main/java/com/example/ayuda_v2/
├── AyudaApplication.kt              # Application class - Inicialización y monitoreo de memoria
├── MainActivity.kt                  # Entry point
│
├── data/                            # CAPA DE DATOS (Model)
│   ├── BookingRepository.kt         # Repository para reservas con Coroutines
│   └── model/
│       ├── Booking.kt               # Modelo de reserva
│       └── Service.kt               # Modelo de servicio + servicios predefinidos
│
├── ui/                              # CAPA DE PRESENTACIÓN (View)
│   ├── components/
│   │   └── ServiceImage.kt          # Componente Coil para imágenes
│   ├── navigation/
│   │   └── NavGraph.kt              # Navegación entre pantallas
│   ├── screens/
│   │   ├── ServicesScreen.kt        # Lista de servicios predefinidos
│   │   ├── BookingFormScreen.kt     # Formulario de reserva
│   │   └── MyBookingsScreen.kt      # Gestión de reservas del usuario
│   ├── state/
│   │   └── UiState.kt               # Sealed class para estados de UI
│   └── theme/
│
└── viewmodel/                       # CAPA DE LÓGICA (ViewModel)
    └── BookingViewModel.kt          # ViewModel principal con Coroutines
```

---

## 🔧 Instalación

### Requisitos Previos
- Android Studio Hedgehog o superior
- JDK 11+
- Android SDK 35+

### Pasos
1. Clonar el repositorio:
```bash
git clone https://github.com/[tu-usuario]/Ayuda_v2.git
```

2. Abrir en Android Studio

3. Sincronizar Gradle

4. Ejecutar en emulador o dispositivo físico

### Generar APK
```bash
./gradlew assembleDebug
```
El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📋 Mejoras Técnicas Implementadas

### 1. Procesamiento Asíncrono (Kotlin Coroutines)
```kotlin
// Ejemplo de BookingViewModel.kt
viewModelScope.launch(exceptionHandler) {
    _uiState.value = UiState.Loading
    withContext(Dispatchers.IO) {
        BookingRepository.add(ctx, booking)
    }
    _uiState.value = UiState.Success(Unit)
}
```
- `Dispatchers.IO` para operaciones de E/S sin bloquear UI
- `viewModelScope` para gestión automática del ciclo de vida
- `CoroutineExceptionHandler` para manejo global de errores

### 2. Debugging y Manejo de Errores
```kotlin
// Try-catch estratégico
try {
    val obj = arr.getJSONObject(i)
    list.add(Booking(...))
} catch (e: JSONException) {
    Log.e(TAG, "Error parsing booking at index $i", e)
}

// Simulación de errores para testing
fun simulateError() {
    viewModelScope.launch(exceptionHandler) {
        throw RuntimeException("Error simulado para testing")
    }
}
```
- Try-catch en operaciones críticas
- Logging estructurado con niveles (Debug, Warning, Error)
- Método `simulateError()` para testing

### 3. Detección de Memory Leaks (LeakCanary)
```kotlin
// build.gradle.kts
debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

// BookingViewModel.kt - Prevención de leaks
private val ctx = application.applicationContext  // ✅ Correcto
```
- LeakCanary integrado en builds de debug
- Uso de `applicationContext` para prevenir leaks
- Monitoreo de eventos de memoria en `AyudaApplication`

### 4. Librería Externa - Coil
```kotlin
// ServiceImage.kt
SubcomposeAsyncImage(
    model = imageUrl,
    loading = { CircularProgressIndicator() },
    error = { DefaultServicePlaceholder() }
)
```
- Carga asíncrona de imágenes
- Caché automático en memoria y disco
- Placeholders durante carga y manejo de errores

### 5. Patrón MVVM
- **Model**: `BookingRepository`, `Booking`, `Service`
- **View**: Screens y Components (Compose)
- **ViewModel**: `BookingViewModel` con StateFlow

---

## 📊 Logcat - Tags para Debugging

```bash
# Filtrar por tags de la aplicación
adb logcat -s BookingRepository:D BookingViewModel:D AyudaApplication:D

# Ver solo errores
adb logcat *:E
```

| Tag | Propósito |
|-----|-----------|
| `BookingRepository` | Operaciones de datos (CRUD) |
| `BookingViewModel` | Lógica de negocio y estados |
| `AyudaApplication` | Eventos de aplicación y memoria |

---

## 📁 Documentación y Evidencias

### Videos Demostrativos
| Archivo | Contenido |
|---------|-----------|
| `docs/screenshots/leaks.mp4` | Demostración de LeakCanary sin memory leaks |
| `docs/screenshots/logcat.mp4` | Uso de Logcat con filtros por TAG |
| `docs/screenshots/Memory.mp4` | Android Profiler mostrando uso de memoria |

### Documentación Técnica
| Archivo | Descripción |
|---------|-------------|
| `docs/PROJECT_REPORT.md` | Informe del proyecto |

---

## 📱 APK

Descarga el APK de la última versión:
- Ubicación: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 Funciones de Testing

El `BookingViewModel` incluye métodos útiles para debugging:

```kotlin
// Simular un error para probar el manejo de excepciones
viewModel.simulateError()

// Limpiar todas las reservas (útil para testing)
viewModel.clearAllBookings()
```

---

## 👨‍💻 Autor

**Proyecto Duoc UC**  
Aplicación desarrollada como parte del curso de desarrollo móvil.

---

## 📄 Licencia

Este proyecto es de uso educativo para Duoc UC.

---

**Versión**: 2.0  
**Última actualización**: Febrero 2026
