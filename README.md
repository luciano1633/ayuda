# 🆘 ¿Necesitas ayuda? - App de Servicios a Domicilio

Aplicación móvil Android desarrollada con Kotlin y Jetpack Compose que permite a los usuarios solicitar y agendar servicios a domicilio de manera rápida y sencilla.

---

## 📱 APK

Descarga directa del APK de la última versión:
- 📦 **[app-debug.apk](apk/app-debug.apk)**

---

## 🎬 Videos Demostrativos

La documentación visual se encuentra en la carpeta `docs/screenshots/`:

| Video | Descripción |
|-------|-------------|
| 📹 [leaks.mp4](docs/screenshots/leaks.mp4) | Demostración de LeakCanary - Detección de memory leaks |
| 📹 [logcat.mp4](docs/screenshots/logcat.mp4) | Uso de Logcat para debugging con TAGs y niveles |
| 📹 [Memory.mp4](docs/screenshots/Memory.mp4) | Android Profiler - Monitoreo de memoria en tiempo real |

---

## 🚀 Características Principales

- **12 servicios predefinidos** organizados por categorías:
  - 🏠 **Hogar**: Electricista, Gasfíter, Cerrajero, Pintor, Jardinero, Limpieza
  - 💻 **Tecnología**: Técnico PC, Técnico Celular
  - 🚗 **Vehículos**: Mecánico, Grúa
  - ❤️ **Salud**: Enfermera a Domicilio, Cuidador de Adulto Mayor

- **Sistema de reservas completo**:
  - Selección de servicio predefinido (el usuario NO escribe servicios)
  - Formulario con validación de datos del cliente
  - Selección de fecha y hora con DatePicker/TimePicker
  - Gestión de reservas: ver, completar (✓ Realizado), cancelar

- **Arquitectura MVVM robusta**:
  - Capas separadas: Model, View, ViewModel
  - Room Database para persistencia local
  - Kotlin Coroutines + Flow para operaciones asíncronas y reactivas
  - StateFlow para estados de UI
  - Inyección de dependencias manual vía ViewModelFactory

---

## 🏗️ Arquitectura Implementada

### Patrón MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────┐
│                    VIEW (UI)                      │
│  ServicesScreen ─ BookingFormScreen ─ MyBookings  │
│          Jetpack Compose + Navigation             │
└──────────────────────┬──────────────────────────┘
                       │ StateFlow / Events
┌──────────────────────┴──────────────────────────┐
│                  VIEWMODEL                        │
│         BookingViewModel + Factory                │
│     Coroutines + ExceptionHandler                 │
└──────────────────────┬──────────────────────────┘
                       │ IBookingRepository
┌──────────────────────┴──────────────────────────┐
│                MODEL (DATA)                       │
│  BookingRepository → BookingDao → Room Database   │
│  Booking, Service, PredefinedServices             │
└─────────────────────────────────────────────────┘
```

### Principios aplicados:
- **Responsabilidad única**: Cada clase tiene un propósito claro
- **Inversión de dependencias**: ViewModel depende de interfaz `IBookingRepository`, no de implementación concreta
- **Separación de capas**: UI no accede a datos directamente

---

## 🛠️ Tecnologías y Componentes Jetpack

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Kotlin** | 2.2.10 | Lenguaje principal |
| **Jetpack Compose** | BOM 2026.01.00 | UI declarativa |
| **Room** | 2.7.1 | Base de datos local (reemplaza SharedPreferences) |
| **ViewModel** | 2.10.0 | Gestión de estado con ciclo de vida |
| **Navigation Compose** | 2.9.6 | Navegación entre pantallas |
| **StateFlow** | - | Flujo reactivo de datos UI |
| **Kotlin Coroutines** | 1.7.3 | Procesamiento asíncrono |
| **Coil** | 2.5.0 | Carga asíncrona de imágenes con caché |
| **LeakCanary** | 2.12 | Detección de memory leaks (debug) |
| **KSP** | 2.2.10-2.0.2 | Procesador de anotaciones para Room |
| **JUnit** | 4.13.2 | Pruebas unitarias |
| **MockK** | 1.13.8 | Mocking para Kotlin |
| **Espresso/Compose Testing** | Latest | Pruebas funcionales de UI |

---

## 📂 Estructura del Proyecto

```
app/src/main/java/com/example/ayuda_v2/
├── AyudaApplication.kt              # Application - Inicialización Room + monitoreo memoria
├── MainActivity.kt                  # Entry point
│
├── data/                            # ═══ CAPA DE DATOS (Model) ═══
│   ├── BookingRepository.kt         # Repositorio + Interfaz IBookingRepository
│   ├── local/
│   │   ├── AppDatabase.kt           # Room Database (singleton)
│   │   ├── BookingDao.kt            # DAO con Flow reactivo
│   │   └── BookingEntity.kt         # Entidad Room + mappers
│   └── model/
│       ├── Booking.kt               # Modelo de dominio + BookingStatus
│       ├── BookingValidator.kt      # Validación de formularios (lógica pura)
│       └── Service.kt               # Modelo de servicio + PredefinedServices
│
├── ui/                              # ═══ CAPA DE PRESENTACIÓN (View) ═══
│   ├── TestTags.kt                  # Constantes centralizadas para test tags
│   ├── components/
│   │   └── ServiceImage.kt          # Componente Coil para imágenes
│   ├── navigation/
│   │   └── NavGraph.kt              # Navigation Compose + ViewModelFactory
│   ├── screens/
│   │   ├── ServicesScreen.kt        # Lista de servicios con filtros
│   │   ├── BookingFormScreen.kt     # Formulario de reserva con validación
│   │   └── MyBookingsScreen.kt      # Gestión de reservas (completar/cancelar)
│   ├── state/
│   │   └── UiState.kt               # Sealed class: Idle/Loading/Success/Error
│   └── theme/
│
└── viewmodel/                       # ═══ CAPA DE LÓGICA (ViewModel) ═══
    └── BookingViewModel.kt          # ViewModel + Factory con inyección de dependencias

app/src/test/                        # ═══ PRUEBAS UNITARIAS ═══
├── viewmodel/
│   └── BookingViewModelTest.kt      # 18 tests: lógica de negocio
└── data/
    ├── BookingRepositoryTest.kt     # 15 tests: operaciones de datos
    └── model/
        ├── BookingValidatorTest.kt  # 28 tests: validación de formularios
        └── PredefinedServicesTest.kt # 17 tests: servicios y modelos

app/src/androidTest/                 # ═══ PRUEBAS FUNCIONALES ═══
├── BookingFlowTest.kt              # 21 tests: flujos de usuario completos
└── ExampleInstrumentedTest.kt      # Test de contexto básico
```

---

## 🔧 Instalación y Ejecución

### Requisitos Previos
- Android Studio Ladybug o superior
- JDK 21+
- Android SDK 35+
- Dispositivo/emulador con Android 15 (API 35)

### Pasos
1. Clonar el repositorio:
```bash
git clone https://github.com/[tu-usuario]/Ayuda_v2.git
```

2. Abrir en Android Studio

3. Sincronizar Gradle (se descargarán Room, KSP, MockK automáticamente)

4. Ejecutar en emulador o dispositivo físico

### Generar APK
```bash
./gradlew assembleDebug
```
El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

### Ejecutar Pruebas Unitarias
```bash
./gradlew testDebugUnitTest
```
Reporte en: `app/build/reports/tests/testDebugUnitTest/index.html`

### Ejecutar Pruebas Funcionales (requiere dispositivo/emulador)
```bash
./gradlew connectedDebugAndroidTest
```

---

## 📋 Mejoras Técnicas Implementadas

### Semana 3: Procesamiento Asíncrono (Kotlin Coroutines)
- `viewModelScope.launch()` para operaciones sin bloquear UI
- `Dispatchers.IO` en el Repository para operaciones de base de datos
- `CoroutineExceptionHandler` para manejo global de errores
- `Flow` reactivo desde Room para actualizaciones automáticas de UI

### Semana 4: Debugging y Manejo de Errores
- **Try-catch** estratégico en operaciones críticas (CRUD de reservas)
- **Logcat** estructurado con niveles (Debug, Info, Warning, Error) y TAGs
- **Simulación de errores** con `simulateError()` para verificar robustez
- TAGs: `BookingRepository`, `BookingViewModel`, `AyudaApplication`

### Semana 5: Gestión de Memoria
- **LeakCanary 2.12** integrado en builds de debug
- **applicationContext** usado en toda la app para prevenir leaks de Activity
- **Monitoreo de memoria** en `AyudaApplication.onTrimMemory()`
- **ViewModel.onCleared()** para logging de liberación de recursos

### Semana 6: Integración de Librerías y Estructura
- **Room Database** reemplaza SharedPreferences (consultas SQL, Flow reactivo, compilación verificada)
- **Coil** para carga asíncrona de imágenes con caché automático
- **KSP** como procesador de anotaciones para Room
- **MVVM** con separación completa de capas
- **ViewModelFactory** para inyección de dependencias

### Semana 7: Arquitectura MVVM y Pruebas
- **Refactorización completa a MVVM**: interfaz `IBookingRepository`, `BookingViewModelFactory`
- **Room + DAO + Entity**: migración de SharedPreferences a base de datos relacional
- **Pruebas unitarias (34 tests)**: BookingViewModelTest (18) + BookingRepositoryTest (15) + ExampleUnitTest (1)
  - MockK para mocking de dependencias
  - coroutines-test para testing asíncrono
- **Pruebas funcionales (12 tests)**: BookingFlowTest (11) + ExampleInstrumentedTest (1)
  - Navegación entre pantallas
  - Visualización de servicios y categorías
  - Formulario de reserva
  - Estado vacío de reservas

### Semana 8: Cierre Técnico y Publicación
- **TestTags centralizados** (`ui/TestTags.kt`): constantes compartidas entre UI y tests (recomendación del profesor)
- **BookingValidator**: clase de validación pura sin dependencias Android, 100% testeable
- **PredefinedServicesTest**: 17 tests para integridad de datos de servicios y modelos
- **BookingValidatorTest**: 28 tests con casos positivos, negativos y valores límite
- **Pruebas funcionales ampliadas**: 21 tests (filtros por categoría, input datos, botón deshabilitado, flujos completos)
- **Total: 99+ pruebas** (78 unitarias + 21 funcionales)
- **APK v3.0** actualizado y disponible en `apk/app-debug.apk`

---

## 🧪 Pruebas Implementadas

### Pruebas Unitarias (JUnit + MockK)

| Clase | Tests | Cobertura |
|-------|-------|-----------|
| `BookingViewModelTest` | 18 | Creación, cancelación, completación de reservas, manejo de errores, Flow |
| `BookingRepositoryTest` | 15 | CRUD, mapeo Entity↔Model, manejo de errores, validación de datos |
| `BookingValidatorTest` | 28 | Validación de nombre, teléfono, dirección, fecha, hora, formulario completo |
| `PredefinedServicesTest` | 17 | Integridad de servicios, categorías, estados, modelos de dominio |
| `ExampleUnitTest` | 1 | Test base |

**Herramientas**: JUnit 4.13.2, MockK 1.13.8, kotlinx-coroutines-test 1.7.3

### Pruebas Funcionales (Compose Testing / Espresso)

| Clase | Tests | Flujo |
|-------|-------|-------|
| `BookingFlowTest` | 21 | Pantalla de servicios, filtros por categoría, navegación, formulario, input datos, Mis Reservas, flujos completos |
| `ExampleInstrumentedTest` | 1 | Verificación de contexto |

**Herramientas**: Compose UI Test JUnit4, AndroidJUnit4, TestTags centralizados

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
| `BookingRepository` | Operaciones de datos (CRUD Room) con tiempos |
| `BookingViewModel` | Lógica de negocio, estados y errores |
| `AyudaApplication` | Eventos de aplicación, memoria y ciclo de vida |

---

## 📁 Documentación y Evidencias

### Videos Demostrativos
| Archivo | Contenido |
|---------|-----------|
| `docs/screenshots/leaks.mp4` | LeakCanary - detección de memory leaks |
| `docs/screenshots/logcat.mp4` | Logcat con filtros por TAG y niveles |
| `docs/screenshots/Memory.mp4` | Android Profiler - uso de memoria |

### Documentación Técnica
| Archivo | Descripción |
|---------|-------------|
| `docs/PROJECT_REPORT.md` | Informe técnico del proyecto |

---

## 👨‍💻 Autor

**Proyecto Duoc UC**  
Aplicación desarrollada como parte del curso de Desarrollo de Aplicaciones Móviles II.

---

## 📄 Licencia

Este proyecto es de uso educativo para Duoc UC.

---

**Versión**: 3.0  
**Última actualización**: Marzo 2026
