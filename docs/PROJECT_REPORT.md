# Informe del prototipo — ¿Necesitas ayuda?

## Índice
- [1. Resumen ejecutivo](#1-resumen-ejecutivo)
- [2. Instrucciones rápidas](#2-instrucciones-rápidas)
- [3. Flujo de la aplicación (usuario)](#3-flujo-de-la-aplicación-usuario)
- [4. Arquitectura y archivos clave](#4-arquitectura-y-archivos-clave)
  - Persistencia
  - Lógica / estado
  - UI
  - Tema / accesibilidad
- [5. Decisiones técnicas relevantes](#5-decisiones-técnicas-relevantes)
- [6. Cómo validar la funcionalidad (pasos manuales)](#6-cómo-validar-la-funcionalidad-pasos-manuales)
- [7. Espacios para capturas (añadir imágenes aquí)](#7-espacios-para-capturas-añadir-imágenes-aquí)
- [8. Apéndice técnico (detallado)](#8-apéndice-técnico-detallado)
  - [8.9 Estructura del proyecto](#89-estructura-del-proyecto)
  - [8.10 Ejemplos de uso del repositorio y ViewModel](#810-ejemplos-de-uso-del-repositorio-y-viewmodel)
  - [8.11 Ejemplo de test unitario para ViewModel](#811-ejemplo-de-test-unitario-para-viewmodel)
  - [8.12 Recomendaciones CI y calidad](#812-recomendaciones-ci-y-calidad)
  - [8.13 Checklist final de entrega](#813-checklist-final-de-entrega)
- [9. Tests instrumentados y cómo ejecutarlos](#9-tests-instrumentados-y-cómo-ejecutarlos)
- [Anexos y rutas relevantes](#anexos-y-rutas-relevantes)

---

# 1. Resumen ejecutivo
La aplicación "¿Necesitas ayuda?" es un prototipo móvil desarrollado con Jetpack Compose que conecta a usuarios con técnicos a domicilio. Implementa:
- Interfaz navegable Home → Detail con `NavHost`.
- Lista de servicios con `LazyColumn` (equivalente moderno a RecyclerView).
- CRUD local (Crear / Leer / Actualizar / Eliminar) usando persistencia local con SharedPreferences (JSON).
- Diálogos para añadir/editar (`AlertDialog`) y confirmación de borrado.
- Principios de accesibilidad (semántica, testTags, tamaños táctiles y jerarquía visual).

Este informe documenta la implementación, cómo validar la funcionalidad y dónde colocar las capturas y el APK para la entrega.

---

# 2. Instrucciones rápidas
- Generar APK debug:

```powershell
.\gradlew.bat :app:assembleDebug
```

- Instalar APK en dispositivo conectado (opcional):

```powershell
.\gradlew.bat :app:installDebug
```

- Ejecutar tests instrumentados (requiere dispositivo/emulador conectado):

```powershell
.\gradlew.bat :app:connectedAndroidTest
```

---

# 3. Flujo de la aplicación (usuario)
- Home: TopAppBar "¿Necesitas ayuda?", lista de servicios, FAB (+) para añadir.
- Añadir servicio: FAB abre diálogo con campos "Título" (obligatorio) y "Subtítulo".
- Detail: muestra ID, título, subtítulo; botones "Editar" y "Eliminar".
- Editar: abre diálogo pre‑relleno; al guardar actualiza la persistencia.
- Eliminar: muestra ConfirmDialog y al confirmar borra el registro.

---

# 4. Arquitectura y archivos clave

## Persistencia
- `app/src/main/java/com/example/ayuda_v2/data/ServicesRepository.kt` — gestiona SharedPreferences (JSON array) con APIs: `getAll`, `saveAll`, `add`, `update`, `delete`.
- Motivación: SharedPreferences es suficiente para el prototipo y facilita pruebas; para escalado producir Room.

## Lógica / estado
- `app/src/main/java/com/example/ayuda_v2/viewmodel/ServicesViewModel.kt` — `AndroidViewModel` que expone `items` (mutableStateListOf) y métodos CRUD que llaman al repositorio.

## UI
- `app/src/main/java/com/example/ayuda_v2/ui/screens/HomeScreen.kt` — lista (`LazyColumn`) y FAB; integra `ServicesViewModel`.
- `app/src/main/java/com/example/ayuda_v2/ui/screens/DetailScreen.kt` — detalle, edición y eliminación.
- `app/src/main/java/com/example/ayuda_v2/ui/components/HelpItem.kt` — fila estilo tarjeta con `testTag` y semántica.
- `app/src/main/java/com/example/ayuda_v2/ui/components/AddEditServiceDialog.kt` — diálogo con validación de título.
- `app/src/main/java/com/example/ayuda_v2/ui/components/ConfirmDialog.kt` — diálogo de confirmación.
- `app/src/main/java/com/example/ayuda_v2/ui/navigation/NavGraph.kt` — rutas y paso del `ServicesViewModel`.

## Tema / accesibilidad
- `app/src/main/java/com/example/ayuda_v2/ui/theme/Color.kt`, `Type.kt`, `Theme.kt`.

---

# 5. Decisiones técnicas relevantes
- Persistencia: elegido SharedPreferences por simplicidad y facilidad de integración con JSON; ventajas: rápido prototipado, sin esquema; limitaciones: no es relacional ni ideal para consultas complejas. Si se requiere escalado, migrar a Room.
- ViewModel: `ServicesViewModel` centraliza el estado y evita acoplamiento UI–datos.
- Pruebas UI: tests instrumentados con Compose testing verifican el flujo crítico (crear → abrir detalle).
- Accesibilidad: se añadieron `contentDescription` y `testTag` para elementos relevantes y se cuidó tamaño táctil y jerarquía tipográfica.

---

# 6. Cómo validar la funcionalidad (pasos manuales)
1. Abrir la app (Home).
2. Pulsar `+` y añadir: Título "Prueba A", Subtítulo "Técnico disponible" → Guardar.
3. Verificar que aparece en la lista.
4. Pulsar el item: abrirá Detail y mostrará `ID: ...` (testTag `detail_id_text`).
5. Pulsar Editar: cambiar título y guardar; verificar cambio en Home.
6. Pulsar Eliminar: confirmar; verificar que desaparece.
7. Reiniciar la app: los cambios deben persistir.

---

# 7. Espacios para capturas (añadir imágenes aquí)
Coloca en `docs/mocks/` los archivos y reemplaza las rutas abajo.

- Pantalla Home

![Home](mocks/home.png)

- Diálogo "Agregar servicio"

![Agregar servicio](mocks/dialog_add.png)

- Pantalla Detail

![Detail](mocks/detail.png)

- Confirmación de borrado

![ConfirmDelete](mocks/confirm_delete.png)

---

# 8. Apéndice técnico (detallado)

Esta sección aporta detalles técnicos pensados para un desarrollador que mantendrá o completará el prototipo: estructura de datos, contratos, API del repositorio, consideraciones de concurrencia y ciclo de vida, y recomendaciones para pruebas e integración continua.

## 8.1 Modelo de datos
Para este prototipo la unidad básica es `HelpModel` (o "Servicio") con la forma mínima:

- id: String (UUID generado al crear)
- title: String (título del servicio, obligatorio)
- subtitle: String (texto opcional descriptivo)

En Kotlin está definido (em ejemplo en `HomeScreen.kt`) como:

```kotlin
data class HelpModel(val id: String, val title: String, val subtitle: String)
```

## 8.2 Formato de persistencia (SharedPreferences / JSON)
Los servicios se almacenan como un array JSON en una única clave de `SharedPreferences`.
Ejemplo de contenido guardado en `KEY_SERVICES`:

```json
[
  { "id": "d290f1ee-6c54-4b01-90e6-d701748f0851", "title": "Electricista - revisión", "subtitle": "Servicio disponible hoy" },
  { "id": "a3f1e8b9-4b1d-4aeb-8e2a-1234567890ab", "title": "Instalación de estufa", "subtitle": "Incluye materiales" }
]
```

Ventajas: simple y fácil de inspeccionar. Limitaciones: operaciones de escritura implican reescribir todo el array; no es eficiente para conjuntos de datos muy grandes.

## 8.3 API del repositorio (`ServicesRepository`)
Contrato público (resumen):

- `getAll(context: Context): List<HelpModel>` — devuelve todos los servicios, o lista vacía si no hay datos.
- `saveAll(context: Context, services: List<HelpModel>)` — sobrescribe la lista completa en SharedPreferences.
- `add(context: Context, service: HelpModel)` — añade un servicio y persiste.
- `update(context: Context, service: HelpModel)` — busca por `id` y reemplaza, luego persiste.
- `delete(context: Context, id: String)` — elimina por `id` y persiste.

Recomendaciones de uso:
- Llamar a estas API desde un ViewModel (ya implementado) para evitar fugas de contexto en UI y manejar estados reactivamente.
- Atrapar y registrar excepciones JSON parsing para evitar crash si los datos en `SharedPreferences` están corruptos.

## 8.4 Consideraciones de concurrencia y lifecycle
- En este prototipo las operaciones son síncronas y rápidas, pero en producción conviene:
  - Ejecutar lecturas/escrituras en un dispatcher IO (coroutines) para no bloquear el hilo UI.
  - Garantizar atomicidad de escrituras: usar `SharedPreferences.edit().apply()` ya es atómico en Android, pero para mayor seguridad en escenarios concurrentes usar un lock en memoria o migrar a Room.
  - Evitar conservar referencias a Context que puedan causar fugas; `ServicesViewModel` actualmente guarda `applicationContext` — recomendable usar `getApplication()` o inyectar `Context` en métodos y no en propiedades.

## 8.5 ViewModel y comunicación UI–datos
- `ServicesViewModel` mantiene una `mutableStateListOf<HelpModel>` que la UI observa; al modificar la lista el Compose UI responde automáticamente.
- Buenas prácticas aplicadas:
  - Centralizar la lógica CRUD en ViewModel (separación de responsabilidades).
  - Delegar persistencia real al repositorio.

## 8.6 Estrategia de testing
- Unit tests recomendados:
  - Pruebas unitarias para `ServicesRepository` usando Robolectric o un wrapper que permita pasar un `Context` de test (o abstraer SharedPreferences para inyectar una implementación de memoria).
  - Pruebas unitarias del ViewModel: aserciones sobre la lista tras llamar `add/update/delete`.
- Instrumented UI tests:
  - Tests con Compose Test que cubran el flujo crítico (crear → ver en lista → abrir detalle → editar → eliminar).
  - Añadimos un test base `HomeScreenTest.kt` que crea un servicio y verifica la navegación; se sugiere agregar un test que verifique la persistencia tras reinicio (simulando recreación de Activity o reinstanciando ViewModel).

## 8.7 Integración continua (CI) y calidad
- Recomendación de pipeline (GitHub Actions / GitLab CI):
  1. Ejecutar `./gradlew assembleDebug` para asegurar que el proyecto compila.
  2. Ejecutar tests unitarios: `./gradlew test`.
  3. Si hay un runner con emulador, ejecutar instrumented tests: `./gradlew connectedAndroidTest`.
  4. Generar artefactos (APK) y adjuntarlos al release o a artefacts de CI.

Archivo de ejemplo para GitHub Actions (sugerencia resumida):

```yml
# .github/workflows/android.yml (resumen)
name: Android CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Build
        run: ./gradlew assembleDebug --no-daemon
      - name: Unit tests
        run: ./gradlew test --no-daemon
      # connectedAndroidTest requiere runner con emulador configurado
```

## 8.8 Robustez y mejoras futuras (roadmap)
- Migrar a Room para persistencia: entidades, DAO y repositorio con coroutines/Flow.
- Introducir inyección de dependencias (Hilt) para facilitar testing e inversión de dependencias.
- Añadir manejo de errores y logs estructurados (Crashlytics/Logging) para diagnósticos.
- Ampliar la suite de tests con pruebas instrumentadas para CRUD completo y pruebas de accesibilidad automatizadas.
- Añadir modo oscuro y pruebas de contraste (herramientas automáticas que verifiquen ratios WCAG).

## 8.9 Estructura del proyecto
```
ayuda_v2/
├── app/
│   ├── src/
│   │   ├── debug/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── example/
│   │   │   │           └── ayuda_v2/
│   │   │   │               ├── data/
│   │   │   │               ├── ui/
│   │   │   │               ├── viewmodel/
│   │   │   │               └── MainActivity.kt
│   │   │   └── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## 8.10 Ejemplos de uso del repositorio y ViewModel

- Obtener todos los servicios en el `ViewModel`:

```kotlin
class ServicesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServicesRepository()
    var items = mutableStateListOf<HelpModel>()
        private set

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            items.clear()
            items.addAll(repository.getAll(getApplication()))
        }
    }

    // Ejemplo de método para añadir un servicio
    fun addService(title: String, subtitle: String) {
        val newService = HelpModel(UUID.randomUUID().toString(), title, subtitle)
        viewModelScope.launch {
            repository.add(getApplication(), newService)
            loadServices() // Recargar servicios tras añadir
        }
    }
}
```

- Llamadas desde la UI (ejemplo en `HomeScreen.kt`):

```kotlin
@Composable
fun HomeScreen(viewModel: ServicesViewModel = viewModel()) {
    val services = viewModel.items

    LazyColumn {
        items(services) { service ->
            HelpItem(service) { /* Navegar a detalle */ }
        }
    }

    FloatingActionButton(onClick = { /* Abrir diálogo de añadir */ }) {
        Icon(Icons.Filled.Add, contentDescription = "Agregar servicio")
    }
}
```

## 8.11 Ejemplo de test unitario para ViewModel

- Test para `ServicesViewModel` usando JUnit y Mockito:

```kotlin
@RunWith(AndroidJUnit4::class)
class ServicesViewModelTest {

    private lateinit var viewModel: ServicesViewModel
    private val repository: ServicesRepository = mock()

    @Before
    fun setUp() {
        // Inyectar repositorio simulado
        viewModel = ServicesViewModel(ApplicationProvider.getApplicationContext())
        viewModel.repository = repository
    }

    @Test
    fun testAddService() = runBlocking {
        // Dado
        val initialSize = viewModel.items.size
        val title = "Nuevo servicio"
        val subtitle = "Descripción del servicio"

        // Cuando
        viewModel.addService(title, subtitle)

        // Entonces
        assertEquals(initialSize + 1, viewModel.items.size)
        verify(repository).add(any(), argThat { this.title == title && this.subtitle == subtitle })
    }
}
```

## 8.12 Recomendaciones CI y calidad
- Asegurarse de que el código sigue las guías de estilo de Kotlin y las mejores prácticas de Android.
- Usar herramientas como Lint y Detekt para análisis estático de código.
- Configurar revisiones de código en merge requests para asegurar la calidad y el cumplimiento de estándares.
- Considerar el uso de SonarQube o similar para análisis de código más profundo y métricas de calidad.

## 8.13 Checklist final de entrega
- [ ] Funcionalidad completa según lo especificado.
- [ ] Código revisado y limpio, sin logs ni código comentado.
- [ ] Tests unitarios e instrumentados cubriendo los casos principales.
- [ ] Documentación actualizada: este informe, comentarios en el código, y documentación adicional si es necesaria.
- [ ] APK generado y probado en dispositivos reales.
- [ ] Todo el artefacto de entrega en la estructura de carpetas adecuada.

---

# 9. Tests instrumentados y cómo ejecutarlos
- Test principal creado: `app/src/androidTest/java/com/example/ayuda_v2/HomeScreenTest.kt` que crea un servicio y abre el detalle.
- Ejecutar:

```powershell
.\gradlew.bat :app:connectedAndroidTest
```

Revisa el reporte HTML en `app/build/reports/androidTests/connected/debug/index.html`.

---

# Anexos y rutas relevantes
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Tests: `app/src/androidTest/java/com/example/ayuda_v2/HomeScreenTest.kt`
- Docs: `docs/PROJECT_REPORT.md` and `docs/mocks/` (evidencias)

