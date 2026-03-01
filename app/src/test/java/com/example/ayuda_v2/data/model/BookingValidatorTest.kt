package com.example.ayuda_v2.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Pruebas unitarias para BookingValidator.
 * Verifica la lógica de validación de formularios de reserva.
 *
 * Cubre casos positivos y negativos para cada campo,
 * incluyendo valores límite y datos inválidos.
 */
class BookingValidatorTest {

    // ========== Tests de validateName ==========

    @Test
    fun `validateName returns valid for normal name`() {
        val result = BookingValidator.validateName("Juan Pérez")
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `validateName returns invalid for empty name`() {
        val result = BookingValidator.validateName("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatorio") })
    }

    @Test
    fun `validateName returns invalid for blank name`() {
        val result = BookingValidator.validateName("   ")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatorio") })
    }

    @Test
    fun `validateName returns invalid for single character`() {
        val result = BookingValidator.validateName("J")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("al menos 2") })
    }

    @Test
    fun `validateName returns valid for 2 characters`() {
        val result = BookingValidator.validateName("Lu")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateName returns invalid for name exceeding 100 chars`() {
        val longName = "A".repeat(101)
        val result = BookingValidator.validateName(longName)
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("100") })
    }

    @Test
    fun `validateName trims whitespace before validation`() {
        val result = BookingValidator.validateName("  Juan  ")
        assertTrue(result.isValid)
    }

    // ========== Tests de validatePhone ==========

    @Test
    fun `validatePhone returns valid for chilean phone`() {
        val result = BookingValidator.validatePhone("+56912345678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validatePhone returns valid for phone with spaces`() {
        val result = BookingValidator.validatePhone("+56 9 1234 5678")
        assertTrue(result.isValid)
    }

    @Test
    fun `validatePhone returns invalid for empty phone`() {
        val result = BookingValidator.validatePhone("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatorio") })
    }

    @Test
    fun `validatePhone returns invalid for too short phone`() {
        val result = BookingValidator.validatePhone("1234567")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("al menos 8") })
    }

    @Test
    fun `validatePhone returns invalid for too long phone`() {
        val result = BookingValidator.validatePhone("1234567890123456")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("15") })
    }

    @Test
    fun `validatePhone returns valid for 8 digit phone`() {
        val result = BookingValidator.validatePhone("12345678")
        assertTrue(result.isValid)
    }

    // ========== Tests de validateAddress ==========

    @Test
    fun `validateAddress returns valid for normal address`() {
        val result = BookingValidator.validateAddress("Av. Siempre Viva 742")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateAddress returns invalid for empty address`() {
        val result = BookingValidator.validateAddress("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatoria") })
    }

    @Test
    fun `validateAddress returns invalid for too short address`() {
        val result = BookingValidator.validateAddress("Ab 1")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("al menos 5") })
    }

    @Test
    fun `validateAddress returns valid for 5 character address`() {
        val result = BookingValidator.validateAddress("Av. 1")
        assertTrue(result.isValid)
    }

    // ========== Tests de validateDate ==========

    @Test
    fun `validateDate returns valid for correct format`() {
        val result = BookingValidator.validateDate("2026-03-15")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateDate returns invalid for empty date`() {
        val result = BookingValidator.validateDate("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatoria") })
    }

    @Test
    fun `validateDate returns invalid for wrong format`() {
        val result = BookingValidator.validateDate("15-03-2026")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("formato") })
    }

    @Test
    fun `validateDate returns invalid for text date`() {
        val result = BookingValidator.validateDate("quince de marzo")
        assertFalse(result.isValid)
    }

    // ========== Tests de validateTime ==========

    @Test
    fun `validateTime returns valid for correct format`() {
        val result = BookingValidator.validateTime("10:30")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateTime returns valid for midnight`() {
        val result = BookingValidator.validateTime("00:00")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateTime returns valid for end of day`() {
        val result = BookingValidator.validateTime("23:59")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateTime returns invalid for empty time`() {
        val result = BookingValidator.validateTime("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatoria") })
    }

    @Test
    fun `validateTime returns invalid for wrong format`() {
        val result = BookingValidator.validateTime("10am")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateTime returns invalid for hour 24`() {
        val result = BookingValidator.validateTime("24:00")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("no es válida") })
    }

    @Test
    fun `validateTime returns invalid for minute 60`() {
        val result = BookingValidator.validateTime("10:60")
        assertFalse(result.isValid)
    }

    // ========== Tests de validateServiceId ==========

    @Test
    fun `validateServiceId returns valid for existing service`() {
        val result = BookingValidator.validateServiceId("electricista")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateServiceId returns invalid for empty id`() {
        val result = BookingValidator.validateServiceId("")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("obligatorio") })
    }

    @Test
    fun `validateServiceId returns invalid for non-existing service`() {
        val result = BookingValidator.validateServiceId("servicio_fantasma")
        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("no existe") })
    }

    // ========== Tests de validateBookingForm ==========

    @Test
    fun `validateBookingForm returns valid for all valid fields`() {
        val result = BookingValidator.validateBookingForm(
            serviceId = "gasfiter",
            customerName = "María López",
            customerPhone = "+56911111111",
            customerAddress = "Calle Principal 100",
            scheduledDate = "2026-04-20",
            scheduledTime = "15:00"
        )
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `validateBookingForm returns invalid with all errors for empty fields`() {
        val result = BookingValidator.validateBookingForm(
            serviceId = "",
            customerName = "",
            customerPhone = "",
            customerAddress = "",
            scheduledDate = "",
            scheduledTime = ""
        )
        assertFalse(result.isValid)
        assertEquals(6, result.errors.size)
    }

    @Test
    fun `validateBookingForm returns invalid with partial errors`() {
        val result = BookingValidator.validateBookingForm(
            serviceId = "electricista",
            customerName = "Juan",
            customerPhone = "", // Invalid
            customerAddress = "Av. Test 123",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        assertFalse(result.isValid)
        assertEquals(1, result.errors.size)
        assertTrue(result.errors.first().contains("teléfono", ignoreCase = true))
    }

    @Test
    fun `validateBookingForm aggregates multiple errors`() {
        val result = BookingValidator.validateBookingForm(
            serviceId = "servicio_fantasma",
            customerName = "J",
            customerPhone = "123",
            customerAddress = "Ab",
            scheduledDate = "invalid",
            scheduledTime = "25:00"
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.size >= 5)
    }
}

