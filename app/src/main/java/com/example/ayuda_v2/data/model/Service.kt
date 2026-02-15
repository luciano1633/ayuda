package com.example.ayuda_v2.data.model

/**
 * Representa un servicio predefinido disponible en la aplicación.
 * Estos servicios vienen precargados y el usuario los selecciona.
 */
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // Emoji o nombre de ícono
    val category: ServiceCategory,
    val estimatedPrice: String? = null
)

/**
 * Categorías de servicios disponibles.
 */
enum class ServiceCategory(val displayName: String) {
    HOGAR("Hogar"),
    TECNOLOGIA("Tecnología"),
    VEHICULOS("Vehículos"),
    SALUD("Salud")
}

/**
 * Lista de servicios predefinidos disponibles en la aplicación.
 */
object PredefinedServices {
    val services = listOf(
        // Servicios del Hogar
        Service(
            id = "electricista",
            name = "Electricista",
            description = "Instalación y reparación eléctrica",
            icon = "⚡",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $25.000"
        ),
        Service(
            id = "gasfiter",
            name = "Gasfíter",
            description = "Reparación de cañerías y grifería",
            icon = "🔧",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $20.000"
        ),
        Service(
            id = "cerrajero",
            name = "Cerrajero",
            description = "Apertura de puertas y cambio de chapas",
            icon = "🔑",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $15.000"
        ),
        Service(
            id = "pintor",
            name = "Pintor",
            description = "Pintura interior y exterior",
            icon = "🎨",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $30.000"
        ),
        Service(
            id = "jardinero",
            name = "Jardinero",
            description = "Mantención de jardines y áreas verdes",
            icon = "🌱",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $18.000"
        ),
        Service(
            id = "limpieza",
            name = "Limpieza",
            description = "Aseo profundo del hogar",
            icon = "🧹",
            category = ServiceCategory.HOGAR,
            estimatedPrice = "Desde $25.000"
        ),

        // Servicios de Tecnología
        Service(
            id = "tecnico_pc",
            name = "Técnico PC",
            description = "Reparación de computadores y notebooks",
            icon = "💻",
            category = ServiceCategory.TECNOLOGIA,
            estimatedPrice = "Desde $20.000"
        ),
        Service(
            id = "tecnico_celular",
            name = "Técnico Celular",
            description = "Reparación de smartphones y tablets",
            icon = "📱",
            category = ServiceCategory.TECNOLOGIA,
            estimatedPrice = "Desde $15.000"
        ),

        // Servicios de Vehículos
        Service(
            id = "mecanico",
            name = "Mecánico",
            description = "Reparación y mantención de vehículos",
            icon = "🚗",
            category = ServiceCategory.VEHICULOS,
            estimatedPrice = "Desde $30.000"
        ),
        Service(
            id = "grua",
            name = "Grúa",
            description = "Servicio de grúa 24/7",
            icon = "🚚",
            category = ServiceCategory.VEHICULOS,
            estimatedPrice = "Desde $40.000"
        ),

        // Servicios de Salud
        Service(
            id = "enfermera",
            name = "Enfermera a Domicilio",
            description = "Atención de enfermería en tu hogar",
            icon = "👩‍⚕️",
            category = ServiceCategory.SALUD,
            estimatedPrice = "Desde $25.000"
        ),
        Service(
            id = "cuidador",
            name = "Cuidador de Adulto Mayor",
            description = "Cuidado y compañía para adultos mayores",
            icon = "🤝",
            category = ServiceCategory.SALUD,
            estimatedPrice = "Desde $20.000/hora"
        )
    )

    fun getById(id: String): Service? = services.find { it.id == id }

    fun getByCategory(category: ServiceCategory): List<Service> =
        services.filter { it.category == category }
}

