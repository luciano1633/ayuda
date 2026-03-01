package com.example.ayuda_v2.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Pruebas unitarias para PredefinedServices y modelos de datos.
 * Verifica la integridad de los servicios predefinidos,
 * las categorías y los estados de reserva.
 */
class PredefinedServicesTest {

    // ========== Tests de servicios predefinidos ==========

    @Test
    fun `services list has exactly 12 services`() {
        assertEquals(12, PredefinedServices.services.size)
    }

    @Test
    fun `all services have unique ids`() {
        val ids = PredefinedServices.services.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `all services have non-empty names`() {
        PredefinedServices.services.forEach { service ->
            assertTrue("Service ${service.id} has empty name", service.name.isNotBlank())
        }
    }

    @Test
    fun `all services have non-empty descriptions`() {
        PredefinedServices.services.forEach { service ->
            assertTrue("Service ${service.id} has empty description", service.description.isNotBlank())
        }
    }

    @Test
    fun `all services have non-empty icons`() {
        PredefinedServices.services.forEach { service ->
            assertTrue("Service ${service.id} has empty icon", service.icon.isNotBlank())
        }
    }

    @Test
    fun `all services have estimated prices`() {
        PredefinedServices.services.forEach { service ->
            assertNotNull("Service ${service.id} has null price", service.estimatedPrice)
            assertTrue("Service ${service.id} has empty price", service.estimatedPrice!!.isNotBlank())
        }
    }

    // ========== Tests de getById ==========

    @Test
    fun `getById returns correct service for electricista`() {
        val service = PredefinedServices.getById("electricista")
        assertNotNull(service)
        assertEquals("Electricista", service?.name)
        assertEquals(ServiceCategory.HOGAR, service?.category)
    }

    @Test
    fun `getById returns correct service for gasfiter`() {
        val service = PredefinedServices.getById("gasfiter")
        assertNotNull(service)
        assertEquals("Gasfíter", service?.name)
    }

    @Test
    fun `getById returns null for non-existing service`() {
        assertNull(PredefinedServices.getById("servicio_inexistente"))
    }

    @Test
    fun `getById returns null for empty string`() {
        assertNull(PredefinedServices.getById(""))
    }

    // ========== Tests de getByCategory ==========

    @Test
    fun `getByCategory HOGAR returns 6 services`() {
        val hogarServices = PredefinedServices.getByCategory(ServiceCategory.HOGAR)
        assertEquals(6, hogarServices.size)
    }

    @Test
    fun `getByCategory TECNOLOGIA returns 2 services`() {
        val techServices = PredefinedServices.getByCategory(ServiceCategory.TECNOLOGIA)
        assertEquals(2, techServices.size)
    }

    @Test
    fun `getByCategory VEHICULOS returns 2 services`() {
        val vehicleServices = PredefinedServices.getByCategory(ServiceCategory.VEHICULOS)
        assertEquals(2, vehicleServices.size)
    }

    @Test
    fun `getByCategory SALUD returns 2 services`() {
        val healthServices = PredefinedServices.getByCategory(ServiceCategory.SALUD)
        assertEquals(2, healthServices.size)
    }

    @Test
    fun `all categories are represented in services`() {
        val categories = PredefinedServices.services.map { it.category }.distinct()
        assertEquals(ServiceCategory.entries.size, categories.size)
    }

    // ========== Tests de ServiceCategory ==========

    @Test
    fun `ServiceCategory has correct display names`() {
        assertEquals("Hogar", ServiceCategory.HOGAR.displayName)
        assertEquals("Tecnología", ServiceCategory.TECNOLOGIA.displayName)
        assertEquals("Vehículos", ServiceCategory.VEHICULOS.displayName)
        assertEquals("Salud", ServiceCategory.SALUD.displayName)
    }

    @Test
    fun `ServiceCategory has exactly 4 entries`() {
        assertEquals(4, ServiceCategory.entries.size)
    }

    // ========== Tests de BookingStatus ==========

    @Test
    fun `BookingStatus has correct display names`() {
        assertEquals("Pendiente", BookingStatus.PENDING.displayName)
        assertEquals("Confirmada", BookingStatus.CONFIRMED.displayName)
        assertEquals("En Progreso", BookingStatus.IN_PROGRESS.displayName)
        assertEquals("Completada", BookingStatus.COMPLETED.displayName)
        assertEquals("Cancelada", BookingStatus.CANCELLED.displayName)
    }

    @Test
    fun `BookingStatus has exactly 5 entries`() {
        assertEquals(5, BookingStatus.entries.size)
    }

    // ========== Tests de Booking model ==========

    @Test
    fun `Booking default status is PENDING`() {
        val booking = Booking(
            id = "test-1",
            serviceId = "electricista",
            serviceName = "Electricista",
            serviceIcon = "⚡",
            customerName = "Test",
            customerPhone = "123",
            customerAddress = "Test Address",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        assertEquals(BookingStatus.PENDING, booking.status)
    }

    @Test
    fun `Booking default notes is empty`() {
        val booking = Booking(
            id = "test-1",
            serviceId = "electricista",
            serviceName = "Electricista",
            serviceIcon = "⚡",
            customerName = "Test",
            customerPhone = "123",
            customerAddress = "Test Address",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        assertEquals("", booking.notes)
    }

    @Test
    fun `Booking createdAt has default value`() {
        val before = System.currentTimeMillis()
        val booking = Booking(
            id = "test-1",
            serviceId = "electricista",
            serviceName = "Electricista",
            serviceIcon = "⚡",
            customerName = "Test",
            customerPhone = "123",
            customerAddress = "Test Address",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        val after = System.currentTimeMillis()
        assertTrue(booking.createdAt in before..after)
    }
}

