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
- [9. Generación y firma del APK](#9-generación-y-firma-del-apk)
- [10. Reflexión e impacto](#10-reflexión-e-impacto)
- [11. Recomendaciones para publicación](#11-recomendaciones-para-publicación)
- [12. Proceso de cierre técnico](#12-proceso-de-cierre-técnico)

---

# 1. Resumen ejecutivo

La aplicación "¿Necesitas ayuda?" es un prototipo móvil desarrollado con **Jetpack Compose** que conecta a usuarios con técnicos a domicilio. A lo largo de las semanas 3 a 8 del curso, se han implementado mejoras progresivas:

| Semana | Mejora | Herramienta/Patrón |
|--------|--------|--------------------|
| 3 | Procesamiento asíncrono | Kotlin Coroutines, Dispatchers.IO, viewModelScope |
| 4 | Debugging y manejo de errores | try-catch, Logcat con TAGs, simulación de errores |
| 5 | Gestión de memoria | LeakCanary 2.12, applicationContext, onTrimMemory |
| 6 | Integración de librerías | Coil (imágenes), Room Database, KSP |
| 7 | Arquitectura y pruebas | MVVM completo, JUnit+MockK, Compose Testing |
| 8 | Cierre técnico y publicación | TestTags centralizados, BookingValidator, APK firmado, +60 pruebas totales |

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
│  Service, PredefinedServices, BookingValidator     │
└──────────────────────────────────────────────────┘
```

### Principios SOLID aplicados:
- **S** - Responsabilidad única: cada clase tiene un propósito claro (BookingValidator solo valida, BookingRepository solo persiste)
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
│       ├── BookingValidator.kt      # Validación de formularios (lógica pura)
│       └── Service.kt               # Service + ServiceCategory + PredefinedServices
├── ui/                              # CAPA VIEW
│   ├── TestTags.kt                  # Constantes centralizadas para test tags
│   ├── components/ServiceImage.kt   # Componente Coil para imágenes
│   ├── navigation/NavGraph.kt       # Navigation Compose + ViewModelFactory
│   ├── screens/
│   │   ├── ServicesScreen.kt        # Lista con filtros + TestTags centralizados
│   │   ├── BookingFormScreen.kt     # Formulario con validación + TestTags
│   │   └── MyBookingsScreen.kt      # Gestión reservas + TestTags
│   ├── state/UiState.kt             # Sealed class: Idle/Loading/Success/Error
│   └── theme/
└── viewmodel/
    └── BookingViewModel.kt          # ViewModel + BookingViewModelFactory

app/src/test/java/com/example/ayuda_v2/   # PRUEBAS UNITARIAS
├── data/
│   ├── BookingRepositoryTest.kt          # 15 tests - repositorio
│   └── model/
│       ├── BookingValidatorTest.kt       # 28 tests - validación de formularios
│       └── PredefinedServicesTest.kt     # 17 tests - servicios y modelos
└── viewmodel/
    └── BookingViewModelTest.kt           # 18 tests - lógica de negocio

app/src/androidTest/java/com/example/ayuda_v2/   # PRUEBAS FUNCIONALES
└── BookingFlowTest.kt                            # 21 tests - flujos de UI
```

---

# 4. Componentes Jetpack utilizados

| Componente | Justificación técnica |
|------------|----------------------|
| **ViewModel** | Sobrevive a cambios de configuración (rotación). Gestiona estado con `StateFlow`. Usa `viewModelScope` para coroutines con ciclo de vida automático. |
| **Room** | Reemplaza SharedPreferences. Ventajas: consultas SQL verificadas en compilación, soporte nativo de `Flow` para actualizaciones reactivas, migraciones de esquema. DAO genera queries optimizadas automáticamente. |
| **Navigation Compose** | Gestiona transiciones entre ServicesScreen → BookingFormScreen → MyBookingsScreen. Soporta argumentos (serviceId) y popBackStack. |
| **StateFlow** | Se usa `StateFlow` (más idiomático en Kotlin que LiveData) para exponer estados reactivos del ViewModel a la UI. `UiState` sealed class maneja Idle/Loading/Success/Error. |
| **Compose** | UI declarativa que se recompone automáticamente al cambiar los StateFlow. TestTags centralizados permiten pruebas funcionales robustas. |

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
- Entidad `BookingEntity` con mappers bidireccionales
- DAO con `Flow<List<BookingEntity>>` para actualizaciones reactivas
- Singleton thread-safe con double-checked locking

### Coil para imágenes
- `SubcomposeAsyncImage` con loading/error states
- Caché automático en memoria y disco
- Integración nativa con Compose lifecycle

## 5.5 Arquitectura MVVM y pruebas (Semana 7)

- **Interfaz `IBookingRepository`**: permite inyectar mocks en tests
- **`BookingViewModelFactory`**: inyección de dependencias manual
- **Migración Room**: de SharedPreferences a base de datos relacional
- **TestTags**: agregados a componentes UI para pruebas funcionales
- **Pruebas unitarias**: 33 tests con JUnit + MockK
- **Pruebas funcionales**: 11 tests con Compose Testing

## 5.6 Cierre técnico y publicación (Semana 8)

### TestTags centralizados (recomendación del profesor)
```kotlin
// ui/TestTags.kt - Constantes centralizadas
object TestTags {
    const val BTN_MY_BOOKINGS = "btn_my_bookings"
    const val BTN_CONFIRM_BOOKING = "btn_confirm_booking"
    const val INPUT_NAME = "input_name"
    // ...
    fun serviceCard(serviceId: String) = "service_card_$serviceId"
    fun bookingCard(bookingId: String) = "booking_card_$bookingId"
}

// Uso en UI:
modifier = Modifier.testTag(TestTags.BTN_CONFIRM_BOOKING)

// Uso en tests:
composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
```

**Beneficio**: Evita errores tipográficos, un solo punto de cambio, consistencia entre UI y tests.

### BookingValidator - Lógica de validación pura
```kotlin
// data/model/BookingValidator.kt
object BookingValidator {
    fun validateName(name: String): ValidationResult { ... }
    fun validatePhone(phone: String): ValidationResult { ... }
    fun validateAddress(address: String): ValidationResult { ... }
    fun validateDate(date: String): ValidationResult { ... }
    fun validateTime(time: String): ValidationResult { ... }
    fun validateBookingForm(...): ValidationResult { ... }
}
```

**Beneficio**: Lógica de negocio pura sin dependencias de Android, 100% testeable con JUnit.

### Pruebas ampliadas
- **+28 tests unitarios** para BookingValidator (casos positivos, negativos, valores límite)
- **+17 tests unitarios** para PredefinedServices y modelos
- **+10 tests funcionales** nuevos (filtros por categoría, input de datos, botón deshabilitado)
- **Total: 99+ pruebas** (78 unitarias + 21 funcionales)

---

# 6. Pruebas unitarias y funcionales

## 6.1 Pruebas unitarias (JUnit + MockK)

### BookingViewModelTest (18 tests)
| Test | Descripción |
|------|-------------|
| `services list contains 12 predefined services` | Verifica carga de servicios |
| `createBooking calls repository add` | Verifica delegación al repositorio |
| `createBooking sets Error for invalid service` | Manejo de servicio inexistente |
| `createBooking preserves customer data` | Integridad de datos del formulario |
| `cancelBooking calls repository cancel` | Verifica cancelación |
| `completeBooking calls updateStatus COMPLETED` | Verifica cambio de estado |
| `deleteBooking calls repository delete` | Verifica eliminación |
| `resetUiState sets state to Idle` | Reset de estado UI |
| `clearAllBookings calls deleteAll` | Limpieza de reservas |
| `bookings flow updates with new data` | Reactividad del Flow |
| ... y 8 tests más | Errores, edge cases, estados |

### BookingRepositoryTest (15 tests)
| Test | Descripción |
|------|-------------|
| `getAllBookingsFlow returns mapped bookings` | Mapeo Entity→Model |
| `add inserts booking entity into dao` | Delegación al DAO |
| `add preserves all booking fields` | Integridad de datos |
| `entity toBooking handles invalid status` | Manejo de datos corruptos |
| `getCount returns 0 when dao throws` | Resiliencia ante errores |
| ... y 10 tests más | CRUD, conversiones, errores |

### BookingValidatorTest (28 tests) — **NUEVO en Semana 8**
| Test | Descripción |
|------|-------------|
| `validateName returns valid for normal name` | Caso positivo |
| `validateName returns invalid for empty` | Campo vacío |
| `validateName returns invalid for single char` | Valor límite |
| `validatePhone returns valid for chilean phone` | Formato +56 |
| `validatePhone returns invalid for too short` | Menos de 8 dígitos |
| `validateDate returns invalid for wrong format` | Formato incorrecto |
| `validateTime returns invalid for hour 24` | Valor fuera de rango |
| `validateBookingForm returns valid for all valid` | Formulario completo |
| `validateBookingForm aggregates multiple errors` | Múltiples errores |
| ... y 19 tests más | Todos los campos y combinaciones |

### PredefinedServicesTest (17 tests) — **NUEVO en Semana 8**
| Test | Descripción |
|------|-------------|
| `services list has exactly 12 services` | Integridad de datos |
| `all services have unique ids` | Sin duplicados |
| `getById returns correct service` | Búsqueda por ID |
| `getByCategory HOGAR returns 6 services` | Filtrado |
| `ServiceCategory has correct display names` | Nombres de categorías |
| `BookingStatus has 5 entries` | Estados de reserva |
| `Booking default status is PENDING` | Valores por defecto |
| ... y 10 tests más | Modelos, categorías, estados |

### Herramientas:
- **JUnit 4.13.2**: Framework de testing
- **MockK 1.13.8**: Mocking nativo de Kotlin (coEvery, coVerify, slot)
- **kotlinx-coroutines-test 1.7.3**: StandardTestDispatcher, advanceUntilIdle

## 6.2 Pruebas funcionales (Compose Testing)

### BookingFlowTest (21 tests) — **Ampliado en Semana 8**
| Test | Flujo verificado |
|------|-----------------|
| `servicesScreen_displaysTitle` | Pantalla principal visible |
| `servicesScreen_displaysServiceCategories` | 4 categorías visibles |
| `servicesScreen_displaysPredefinedServices` | Servicios listados |
| `servicesScreen_displaysServicePrices` | Precios visibles (**nuevo**) |
| `servicesScreen_filterByCategory` | Filtro por Tecnología |
| `servicesScreen_filterByCategory_showsVehiculos` | Filtro Vehículos (**nuevo**) |
| `servicesScreen_filterByCategory_showsSalud` | Filtro Salud (**nuevo**) |
| `servicesScreen_toggleFilter_showsAll` | Toggle de filtros (**nuevo**) |
| `servicesScreen_clickService_navigatesToBookingForm` | Navegación a formulario |
| `bookingForm_displaysServiceInfo` | Info del servicio en formulario |
| `bookingForm_displaysFormFields` | Campos del formulario |
| `bookingForm_confirmButton_disabledWhenEmpty` | Validación botón (**nuevo**) |
| `bookingForm_canInputCustomerData` | Ingreso de datos (**nuevo**) |
| `bookingForm_backButton_returnsToServices` | Navegación atrás |
| `myBookings_navigation_works` | Navegar a Mis Reservas |
| `myBookings_showsEmptyState` | Estado vacío |
| `myBookings_showsEmptyStateMessage` | Mensaje estado vacío (**nuevo**) |
| `myBookings_backButton_returnsToServices` | Navegación atrás |
| `fullFlow_navigateToServiceAndBackToHome` | Flujo completo ida y vuelta (**nuevo**) |
| `fullFlow_navigateToBookingsAndBack` | Flujo reservas completo (**nuevo**) |

### Herramientas:
- **Compose UI Test JUnit4**: createAndroidComposeRule
- **AndroidJUnit4**: Runner para tests instrumentados
- **TestTags centralizados**: Constantes compartidas UI↔Tests

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

# 9. Generación y firma del APK

## Proceso de generación
1. Configuración de `versionCode = 3` y `versionName = "3.0"` en `build.gradle.kts`
2. Verificación de `applicationId`, `minSdk`, `targetSdk` y permisos
3. Generación del APK de debug: `./gradlew assembleDebug`
4. APK disponible en: `apk/app-debug.apk`

## Configuración del APK
```kotlin
defaultConfig {
    applicationId = "com.example.ayuda_v2"
    minSdk = 35
    targetSdk = 36
    versionCode = 3
    versionName = "3.0"
}
```

---

# 10. Reflexión e impacto

## Impacto en calidad
- **Room** elimina errores de parsing JSON manual y garantiza integridad de datos
- **MockK** permite verificar la lógica de negocio aislada de Android
- **Compose Testing** valida los flujos reales del usuario
- **BookingValidator** centraliza la validación con 28 pruebas que cubren valores límite
- **TestTags centralizados** eliminan errores tipográficos y facilitan mantenimiento

## Impacto en escalabilidad
- **IBookingRepository** permite migrar a API REST (Retrofit) sin cambiar el ViewModel
- **ViewModelFactory** facilita migración futura a Hilt/Dagger
- **Flow reactivo** de Room actualiza la UI automáticamente sin polling
- **BookingValidator** reutilizable en cualquier capa (ViewModel, UI, backend)

## Impacto en experiencia de usuario
- **Coroutines** mantienen la UI fluida durante operaciones de base de datos
- **UiState sealed class** permite mostrar loading/error/success consistentemente
- **LeakCanary** previene degradación de rendimiento por memory leaks
- **Validación en formulario** evita que el usuario envíe datos incompletos

---

# 11. Recomendaciones para publicación

1. **Inyección de dependencias**: Migrar de ViewModelFactory manual a Hilt para mayor escalabilidad
2. **API REST**: Integrar Retrofit para sincronizar reservas con un backend
3. **Coil**: Configurar caché personalizada y manejo de errores avanzado para imágenes de perfil
4. **CI/CD**: Configurar GitHub Actions para compilación y tests automáticos en cada push
5. **Seguridad**: Agregar cifrado de Room database para datos sensibles del usuario
6. **Accesibilidad**: Ampliar contentDescription y pruebas de contraste WCAG
7. **ProGuard/R8**: Habilitar minificación para APK de release

---

# 12. Proceso de cierre técnico

## Resumen de evolución del proyecto

### Semana 3-4: Fundamentos técnicos
- Procesamiento asíncrono con Coroutines evita bloqueo de UI
- Debugging estructurado con Logcat permite trazabilidad completa

### Semana 5-6: Robustez y extensibilidad
- LeakCanary detecta y previene memory leaks en producción
- Room Database reemplaza SharedPreferences para escalabilidad
- Coil gestiona imágenes con lifecycle-awareness

### Semana 7: Arquitectura profesional
- MVVM con Repository Pattern desacopla capas
- 34 pruebas unitarias y 11 funcionales verifican estabilidad

### Semana 8: Cierre y publicación
- TestTags centralizados (recomendación del profesor aplicada)
- BookingValidator con lógica pura 100% testeable
- Cobertura ampliada a 99+ pruebas totales (78 unitarias + 21 funcionales)
- APK v3.0 generado y disponible para descarga
- Documentación técnica completa y actualizada
