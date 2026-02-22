# Informe Técnico — ¿Necesitas ayuda?

## Índice
- [1. Resumen ejecutivo](#1-resumen-ejecutivo)
- [2. Flujo funcional elegido](#2-flujo-funcional-elegido)
- [3. Arquitectura MVVM implementada](#3-arquitectura-mvvm-implementada)
- [4. Componentes Jetpack utilizados](#4-componentes-jetpack-utilizados)
- [5. Mejoras implementadas](#5-mejoras-implementadas)
- [6. Pruebas unitarias y funcionales](#6-pruebas-unitarias-y-funcionales)
- [7. Instrucciones para ejecutar el proyecto](#7-instrucciones-para-ejecutar-el-proyecto)
- [8. Evidencias de pruebas](#8-evidencias-de-pruebas)
- [9. Reflexión e impacto](#9-reflexión-e-impacto)
- [10. Recomendaciones para publicación](#10-recomendaciones-para-publicación)

---

# 1. Resumen ejecutivo

La aplicación "¿Necesitas ayuda?" es un prototipo móvil desarrollado con **Jetpack Compose** que conecta a usuarios con técnicos a domicilio. A lo largo de las semanas 3 a 7 del curso, se han implementado mejoras progresivas:

| Semana | Mejora | Herramienta/Patrón |
|--------|--------|--------------------|
| 3 | Procesamiento asíncrono | Kotlin Coroutines, Dispatchers.IO, viewModelScope |
| 4 | Debugging y manejo de errores | try-catch, Logcat con TAGs, simulación de errores |
| 5 | Gestión de memoria | LeakCanary 2.12, applicationContext, onTrimMemory |
| 6 | Integración de librerías | Coil (imágenes), Room Database, KSP |
| 7 | Arquitectura y pruebas | MVVM completo, JUnit+MockK, Compose Testing |

---

# 2. Flujo funcional elegido

## Sistema completo de reservas de servicios

Se seleccionó el **flujo de reservas** como flujo crítico porque:

1. **Impacto directo en UX**: Es la funcionalidad principal de la app — el usuario selecciona un servicio predefinido, completa un formulario y agenda una hora.
2. **Complejidad técnica**: Involucra operaciones CRUD, persistencia local, validación de formularios, navegación entre pantallas y gestión de estados.
3. **Permite aplicar múltiples técnicas avanzadas**: asincronía (Coroutines/Flow), debugging (try-catch/Logcat), memoria (LeakCanary), base de datos (Room) y pruebas (unitarias + funcionales).

### Pantallas del flujo:
- **ServicesScreen**: Lista de 12 servicios predefinidos con filtros por categoría (Hogar, Tecnología, Vehículos, Salud)
- **BookingFormScreen**: Formulario con validación (nombre, teléfono, dirección, fecha/hora)
- **MyBookingsScreen**: Gestión de reservas — marcar como realizado (✓) o cancelar

---

# 3. Arquitectura MVVM implementada

## Enfoque arquitectónico

Se implementó el patrón **MVVM (Model-View-ViewModel)** con separación estricta de responsabilidades:

```
┌──────────────────────────────────────────────────┐
│                   VIEW (UI)                       │
│  ServicesScreen ─ BookingFormScreen ─ MyBookings   │
│         Jetpack Compose + Navigation              │
└─────────────────────┬────────────────────────────┘
                      │ StateFlow / UiState
┌─────────────────────┴────────────────────────────┐
│                 VIEWMODEL                         │
│       BookingViewModel + ViewModelFactory         │
│    Coroutines + CoroutineExceptionHandler         │
└─────────────────────┬────────────────────────────┘
                      │ IBookingRepository (interfaz)
┌─────────────────────┴────────────────────────────┐
│               MODEL (DATA)                        │
│  BookingRepository → BookingDao → Room Database   │
│  BookingEntity ↔ Booking (mappers)                │
│  Service, PredefinedServices                      │
└──────────────────────────────────────────────────┘
```

### Principios SOLID aplicados:
- **S** - Responsabilidad única: cada clase tiene un propósito claro
- **O** - Abierto/cerrado: se pueden agregar servicios sin modificar el ViewModel
- **L** - Sustitución de Liskov: `IBookingRepository` permite sustituir la implementación
- **I** - Segregación de interfaces: interfaz `IBookingRepository` con métodos precisos
- **D** - Inversión de dependencias: ViewModel depende de la interfaz, no de Room directamente

### Estructura de archivos:
```
app/src/main/java/com/example/ayuda_v2/
├── AyudaApplication.kt              # Inicialización Room + monitoreo memoria
├── MainActivity.kt                  # Entry point
├── data/                            # CAPA MODEL
│   ├── BookingRepository.kt         # IBookingRepository + BookingRepository
│   ├── local/
│   │   ├── AppDatabase.kt           # Room Database (singleton)
│   │   ├── BookingDao.kt            # DAO con Flow reactivo
│   │   └── BookingEntity.kt         # Entidad Room + mappers toBooking/toEntity
│   └── model/
│       ├── Booking.kt               # Modelo de dominio + BookingStatus enum
│       └── Service.kt               # Service + ServiceCategory + PredefinedServices
├── ui/                              # CAPA VIEW
│   ├── components/ServiceImage.kt   # Componente Coil para imágenes
│   ├── navigation/NavGraph.kt       # Navigation Compose + ViewModelFactory
│   ├── screens/
│   │   ├── ServicesScreen.kt        # Lista con filtros + testTags
│   │   ├── BookingFormScreen.kt     # Formulario con validación + testTags
│   │   └── MyBookingsScreen.kt      # Gestión reservas + testTags
│   ├── state/UiState.kt             # Sealed class: Idle/Loading/Success/Error
│   └── theme/
└── viewmodel/
    └── BookingViewModel.kt          # ViewModel + BookingViewModelFactory
```

---

# 4. Componentes Jetpack utilizados

| Componente | Justificación técnica |
|------------|----------------------|
| **ViewModel** | Sobrevive a cambios de configuración (rotación). Gestiona estado con `StateFlow`. Usa `viewModelScope` para coroutines con ciclo de vida automático. |
| **Room** | Reemplaza SharedPreferences. Ventajas: consultas SQL verificadas en compilación, soporte nativo de `Flow` para actualizaciones reactivas, migraciones de esquema. DAO genera queries optimizadas automáticamente. |
| **Navigation Compose** | Gestiona transiciones entre ServicesScreen → BookingFormScreen → MyBookingsScreen. Soporta argumentos (serviceId) y popBackStack. |
| **LiveData/StateFlow** | Se usa `StateFlow` (más idiomático en Kotlin) para exponer estados reactivos del ViewModel a la UI. `UiState` sealed class maneja Idle/Loading/Success/Error. |
| **Compose** | UI declarativa que se recompone automáticamente al cambiar los StateFlow. TestTags permiten pruebas funcionales. |

---

# 5. Mejoras implementadas

## 5.1 Procesamiento asíncrono (Semana 3)

**Herramienta**: Kotlin Coroutines

```kotlin
// BookingViewModel.kt - Coroutines sin bloquear UI
viewModelScope.launch(exceptionHandler) {
    _uiState.value = UiState.Loading
    repository.add(booking)  // suspend fun ejecutada en IO
    _uiState.value = UiState.Success(Unit)
}
```

```kotlin
// BookingRepository.kt - Dispatchers.IO para operaciones de BD
override suspend fun add(booking: Booking) {
    withContext(Dispatchers.IO) {
        bookingDao.insert(booking.toEntity())
    }
}
```

**Justificación**: `viewModelScope` cancela automáticamente las coroutines cuando el ViewModel se destruye. `Dispatchers.IO` previene ANRs ejecutando operaciones de BD en un hilo de background.

## 5.2 Debugging y manejo de errores (Semana 4)

**Herramientas**: try-catch, Logcat, CoroutineExceptionHandler

```kotlin
// Logging estructurado con niveles y TAGs
Log.d(TAG, "Adding booking: ${booking.id}")      // Debug
Log.i(TAG, "App initialized successfully")        // Info
Log.w(TAG, "Memory trim: Running low")            // Warning
Log.e(TAG, "Error adding booking", exception)     // Error

// CoroutineExceptionHandler global
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e(TAG, "Error in coroutine", throwable)
    _uiState.value = UiState.Error(throwable.message ?: "Error desconocido")
}

// Simulación de errores para testing
fun simulateError() {
    viewModelScope.launch(exceptionHandler) {
        throw RuntimeException("Error simulado para testing")
    }
}
```

**TAGs utilizados**: `BookingRepository`, `BookingViewModel`, `AyudaApplication`

## 5.3 Gestión de memoria (Semana 5)

**Herramienta**: LeakCanary 2.12

```kotlin
// build.gradle.kts - Solo en debug builds
debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

// AyudaApplication.kt - Monitoreo de memoria
override fun onLowMemory() {
    Log.w(TAG, "System is running low on memory")
}
override fun onTrimMemory(level: Int) { ... }
```

**Correcciones aplicadas**:
- Uso de `applicationContext` en vez de `Activity context` para prevenir leaks
- ViewModel NO retiene referencias a Activity/Fragment
- `ViewModel.onCleared()` con logging para verificar liberación de recursos

## 5.4 Integración de librerías (Semana 6)

### Room Database
```kotlin
// Entidad Room
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val serviceId: String,
    val serviceName: String,
    // ... más campos
)

// DAO con Flow reactivo
@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)
}
```

**Justificación**: Room verifica queries SQL en tiempo de compilación, soporta Flow nativo para actualizaciones reactivas de UI, y gestiona migraciones de esquema.

### Coil para imágenes
```kotlin
// ServiceImage.kt
SubcomposeAsyncImage(
    model = imageUrl,
    loading = { CircularProgressIndicator() },
    error = { DefaultServicePlaceholder() }
)
```

**Justificación**: Coil es nativo de Kotlin, soporta Coroutines, tiene integración nativa con Compose, caché automático en memoria y disco, y previene memory leaks automáticamente.

## 5.5 Arquitectura MVVM y pruebas (Semana 7)

- **Interfaz `IBookingRepository`**: permite inyectar mocks en tests
- **`BookingViewModelFactory`**: inyección de dependencias manual
- **Migración Room**: de SharedPreferences a base de datos relacional
- **TestTags**: agregados a componentes UI para pruebas funcionales
- **Pruebas unitarias**: 33 tests con JUnit + MockK
- **Pruebas funcionales**: 11 tests con Compose Testing

---

# 6. Pruebas unitarias y funcionales

## 6.1 Pruebas unitarias (JUnit + MockK)

### BookingViewModelTest (18 tests)
```kotlin
@Test
fun `createBooking calls repository add on success`() = runTest {
    coEvery { repository.add(any()) } just Runs
    viewModel.createBooking(serviceId = "electricista", ...)
    advanceUntilIdle()
    coVerify(exactly = 1) { repository.add(match { it.serviceId == "electricista" }) }
}

@Test
fun `createBooking sets Error state for invalid service id`() = runTest {
    viewModel.createBooking(serviceId = "servicio_fantasma", ...)
    advanceUntilIdle()
    assertTrue(viewModel.uiState.value is UiState.Error)
}
```

| Test | Descripción |
|------|-------------|
| `services list contains 12 predefined services` | Verifica carga de servicios |
| `createBooking calls repository add` | Verifica delegación al repositorio |
| `createBooking sets Error for invalid service` | Manejo de servicio inexistente |
| `cancelBooking calls repository cancel` | Verifica cancelación |
| `completeBooking calls updateStatus COMPLETED` | Verifica cambio de estado |
| `bookings flow updates with new data` | Verifica reactividad del Flow |
| ... y 12 tests más | |

### BookingRepositoryTest (15 tests)
| Test | Descripción |
|------|-------------|
| `getAllBookingsFlow returns mapped bookings` | Mapeo Entity→Model |
| `add inserts booking entity into dao` | Delegación al DAO |
| `add preserves all booking fields` | Integridad de datos |
| `entity toBooking handles invalid status` | Manejo de datos corruptos |
| `getCount returns 0 when dao throws` | Resiliencia ante errores |
| ... y 10 tests más | |

### Herramientas:
- **JUnit 4.13.2**: Framework de testing
- **MockK 1.13.8**: Mocking nativo de Kotlin (coEvery, coVerify, slot)
- **kotlinx-coroutines-test 1.7.3**: StandardTestDispatcher, advanceUntilIdle

## 6.2 Pruebas funcionales (Compose Testing)

### BookingFlowTest (11 tests)
```kotlin
@Test
fun servicesScreen_clickService_navigatesToBookingForm() {
    composeTestRule.onNodeWithTag("service_card_electricista").performClick()
    composeTestRule.onNodeWithText("Agendar Servicio").assertIsDisplayed()
}
```

| Test | Flujo verificado |
|------|-----------------|
| `servicesScreen_displaysTitle` | Pantalla principal visible |
| `servicesScreen_displaysServiceCategories` | 4 categorías visibles |
| `servicesScreen_displaysPredefinedServices` | Servicios listados |
| `servicesScreen_filterByCategory` | Filtro por Tecnología |
| `servicesScreen_clickService_navigatesToBookingForm` | Navegación a formulario |
| `bookingForm_displaysServiceInfo` | Info del servicio en formulario |
| `bookingForm_displaysFormFields` | Campos del formulario |
| `bookingForm_backButton_returnsToServices` | Navegación atrás |
| `myBookings_navigation_works` | Navegar a Mis Reservas |
| `myBookings_showsEmptyState` | Estado vacío |
| `myBookings_backButton_returnsToServices` | Navegación atrás |

### Herramientas:
- **Compose UI Test JUnit4**: createAndroidComposeRule
- **AndroidJUnit4**: Runner para tests instrumentados

---

# 7. Instrucciones para ejecutar el proyecto

## Requisitos
- Android Studio Ladybug o superior
- JDK 21+
- Android SDK 35+ (API 35)

## Compilar y ejecutar
```bash
./gradlew assembleDebug
```

## Ejecutar pruebas unitarias
```bash
./gradlew testDebugUnitTest
```
Reporte HTML: `app/build/reports/tests/testDebugUnitTest/index.html`

## Ejecutar pruebas funcionales (requiere dispositivo/emulador)
```bash
./gradlew connectedDebugAndroidTest
```
Reporte HTML: `app/build/reports/androidTests/connected/debug/index.html`

## APK descargable
Ubicación en el repositorio: `apk/app-debug.apk`

---

# 8. Evidencias de pruebas

Las evidencias visuales se encuentran en el repositorio:

| Archivo | Contenido |
|---------|-----------|
| `docs/screenshots/leaks.mp4` | LeakCanary - detección y ausencia de memory leaks |
| `docs/screenshots/logcat.mp4` | Logcat con filtros por TAG y niveles de log |
| `docs/screenshots/Memory.mp4` | Android Profiler - monitoreo de memoria en tiempo real |

---

# 9. Reflexión e impacto

## Impacto en calidad
- **Room** elimina errores de parsing JSON manual y garantiza integridad de datos
- **MockK** permite verificar la lógica de negocio aislada de Android
- **Compose Testing** valida los flujos reales del usuario

## Impacto en escalabilidad
- **IBookingRepository** permite migrar a API REST (Retrofit) sin cambiar el ViewModel
- **ViewModelFactory** facilita migración futura a Hilt/Dagger
- **Flow reactivo** de Room actualiza la UI automáticamente sin polling

## Impacto en experiencia de usuario
- **Coroutines** mantienen la UI fluida durante operaciones de base de datos
- **UiState sealed class** permite mostrar loading/error/success consistentemente
- **LeakCanary** previene degradación de rendimiento por memory leaks

---

# 10. Recomendaciones para publicación

1. **Inyección de dependencias**: Migrar de ViewModelFactory manual a Hilt para mayor escalabilidad
2. **API REST**: Integrar Retrofit para sincronizar reservas con un backend
3. **Coil**: Configurar caché personalizada y manejo de errores avanzado para imágenes de perfil
4. **CI/CD**: Configurar GitHub Actions para compilación y tests automáticos en cada push
5. **Seguridad**: Agregar cifrado de Room database para datos sensibles del usuario
6. **Accesibilidad**: Ampliar contentDescription y pruebas de contraste WCAG
