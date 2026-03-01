package com.example.ayuda_v2.data.model

/**
 * Clase de utilidad para validar los datos de entrada del formulario de reserva.
 * Contiene lógica de negocio pura (sin dependencias de Android),
 * lo que facilita las pruebas unitarias.
 *
 * Principio de responsabilidad única (SRP): esta clase solo valida datos.
 */
object BookingValidator {

    /**
     * Resultado de validación que contiene si es válido y mensajes de error.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )

    /**
     * Valida el nombre del cliente.
     * - No debe estar vacío
     * - Debe tener al menos 2 caracteres
     * - No debe exceder 100 caracteres
     */
    fun validateName(name: String): ValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("El nombre es obligatorio"))
            trimmed.length < 2 -> ValidationResult(false, listOf("El nombre debe tener al menos 2 caracteres"))
            trimmed.length > 100 -> ValidationResult(false, listOf("El nombre no debe exceder 100 caracteres"))
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida el teléfono del cliente.
     * - No debe estar vacío
     * - Debe tener entre 8 y 15 dígitos (formato chileno o internacional)
     * - Solo permite dígitos y el signo +
     */
    fun validatePhone(phone: String): ValidationResult {
        val trimmed = phone.trim()
        val digitsOnly = trimmed.filter { it.isDigit() }
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("El teléfono es obligatorio"))
            digitsOnly.length < 8 -> ValidationResult(false, listOf("El teléfono debe tener al menos 8 dígitos"))
            digitsOnly.length > 15 -> ValidationResult(false, listOf("El teléfono no debe exceder 15 dígitos"))
            !trimmed.all { it.isDigit() || it == '+' || it == ' ' } ->
                ValidationResult(false, listOf("El teléfono solo puede contener números, + y espacios"))
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida la dirección del cliente.
     * - No debe estar vacía
     * - Debe tener al menos 5 caracteres
     */
    fun validateAddress(address: String): ValidationResult {
        val trimmed = address.trim()
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("La dirección es obligatoria"))
            trimmed.length < 5 -> ValidationResult(false, listOf("La dirección debe tener al menos 5 caracteres"))
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida la fecha seleccionada.
     * - No debe estar vacía
     * - Debe tener formato YYYY-MM-DD
     */
    fun validateDate(date: String): ValidationResult {
        val trimmed = date.trim()
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("La fecha es obligatoria"))
            !trimmed.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) ->
                ValidationResult(false, listOf("La fecha debe tener formato YYYY-MM-DD"))
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida la hora seleccionada.
     * - No debe estar vacía
     * - Debe tener formato HH:MM
     */
    fun validateTime(time: String): ValidationResult {
        val trimmed = time.trim()
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("La hora es obligatoria"))
            !trimmed.matches(Regex("\\d{2}:\\d{2}")) ->
                ValidationResult(false, listOf("La hora debe tener formato HH:MM"))
            else -> {
                val parts = trimmed.split(":")
                val hour = parts[0].toIntOrNull() ?: -1
                val minute = parts[1].toIntOrNull() ?: -1
                if (hour !in 0..23 || minute !in 0..59) {
                    ValidationResult(false, listOf("La hora no es válida"))
                } else {
                    ValidationResult(true)
                }
            }
        }
    }

    /**
     * Valida el ID del servicio.
     * - No debe estar vacío
     * - Debe existir en los servicios predefinidos
     */
    fun validateServiceId(serviceId: String): ValidationResult {
        val trimmed = serviceId.trim()
        return when {
            trimmed.isBlank() -> ValidationResult(false, listOf("El servicio es obligatorio"))
            PredefinedServices.getById(trimmed) == null ->
                ValidationResult(false, listOf("El servicio seleccionado no existe"))
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida todos los campos del formulario de reserva.
     * Retorna un resultado agregado con todos los errores encontrados.
     */
    fun validateBookingForm(
        serviceId: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        scheduledDate: String,
        scheduledTime: String
    ): ValidationResult {
        val validations = listOf(
            validateServiceId(serviceId),
            validateName(customerName),
            validatePhone(customerPhone),
            validateAddress(customerAddress),
            validateDate(scheduledDate),
            validateTime(scheduledTime)
        )

        val allErrors = validations.flatMap { it.errors }
        return ValidationResult(
            isValid = allErrors.isEmpty(),
            errors = allErrors
        )
    }
}

