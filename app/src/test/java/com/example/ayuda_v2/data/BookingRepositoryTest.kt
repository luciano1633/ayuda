package com.example.ayuda_v2.data

import com.example.ayuda_v2.data.local.BookingDao
import com.example.ayuda_v2.data.local.BookingEntity
import com.example.ayuda_v2.data.local.toEntity
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para BookingRepository.
 * Verifica que el repositorio delega correctamente las operaciones al DAO
 * y transforma los datos entre entidades Room y modelos de dominio.
 *
 * Usa MockK para mockear BookingDao.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookingRepositoryTest {

    private lateinit var dao: BookingDao
    private lateinit var repository: BookingRepository
    private val testDispatcher = StandardTestDispatcher()

    private val sampleBooking = Booking(
        id = "booking-001",
        serviceId = "gasfiter",
        serviceName = "Gasfíter",
        serviceIcon = "🔧",
        customerName = "María González",
        customerPhone = "+56987654321",
        customerAddress = "Calle Las Flores 456",
        scheduledDate = "2026-04-10",
        scheduledTime = "14:30",
        notes = "Grifo de la cocina",
        status = BookingStatus.PENDING,
        createdAt = 2000L
    )

    private val sampleEntity = BookingEntity(
        id = "booking-001",
        serviceId = "gasfiter",
        serviceName = "Gasfíter",
        serviceIcon = "🔧",
        customerName = "María González",
        customerPhone = "+56987654321",
        customerAddress = "Calle Las Flores 456",
        scheduledDate = "2026-04-10",
        scheduledTime = "14:30",
        notes = "Grifo de la cocina",
        status = "PENDING",
        createdAt = 2000L
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock android.util.Log for JVM unit tests
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        dao = mockk(relaxed = true)
        repository = BookingRepository(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    // ========== Tests de getAllBookingsFlow ==========

    @Test
    fun `getAllBookingsFlow returns mapped bookings from dao`() = runTest {
        every { dao.getAllBookings() } returns flowOf(listOf(sampleEntity))

        val bookings = repository.getAllBookingsFlow().first()

        assertEquals(1, bookings.size)
        assertEquals("booking-001", bookings[0].id)
        assertEquals("Gasfíter", bookings[0].serviceName)
        assertEquals(BookingStatus.PENDING, bookings[0].status)
    }

    @Test
    fun `getAllBookingsFlow returns empty list when dao has no data`() = runTest {
        every { dao.getAllBookings() } returns flowOf(emptyList())

        val bookings = repository.getAllBookingsFlow().first()

        assertTrue(bookings.isEmpty())
    }

    @Test
    fun `getAllBookingsFlow maps multiple bookings correctly`() = runTest {
        val entities = listOf(
            sampleEntity,
            sampleEntity.copy(id = "booking-002", serviceName = "Electricista", status = "COMPLETED")
        )
        every { dao.getAllBookings() } returns flowOf(entities)

        val bookings = repository.getAllBookingsFlow().first()

        assertEquals(2, bookings.size)
        assertEquals(BookingStatus.COMPLETED, bookings[1].status)
    }

    // ========== Tests de add ==========

    @Test
    fun `add inserts booking entity into dao`() = runTest {
        coEvery { dao.insert(any()) } just Runs

        repository.add(sampleBooking)
        advanceUntilIdle()

        coVerify(exactly = 1) { dao.insert(match { it.id == "booking-001" }) }
    }

    @Test
    fun `add preserves all booking fields in entity`() = runTest {
        val capturedEntity = slot<BookingEntity>()
        coEvery { dao.insert(capture(capturedEntity)) } just Runs

        repository.add(sampleBooking)
        advanceUntilIdle()

        val entity = capturedEntity.captured
        assertEquals("booking-001", entity.id)
        assertEquals("gasfiter", entity.serviceId)
        assertEquals("María González", entity.customerName)
        assertEquals("+56987654321", entity.customerPhone)
        assertEquals("Calle Las Flores 456", entity.customerAddress)
        assertEquals("2026-04-10", entity.scheduledDate)
        assertEquals("14:30", entity.scheduledTime)
        assertEquals("Grifo de la cocina", entity.notes)
        assertEquals("PENDING", entity.status)
    }

    @Test(expected = RuntimeException::class)
    fun `add throws when dao insert fails`() = runTest {
        coEvery { dao.insert(any()) } throws RuntimeException("Insert failed")

        repository.add(sampleBooking)
        advanceUntilIdle()
    }

    // ========== Tests de updateStatus ==========

    @Test
    fun `updateStatus calls dao with correct params`() = runTest {
        coEvery { dao.updateStatus(any(), any()) } just Runs

        repository.updateStatus("booking-001", BookingStatus.COMPLETED)
        advanceUntilIdle()

        coVerify(exactly = 1) { dao.updateStatus("booking-001", "COMPLETED") }
    }

    @Test
    fun `updateStatus to CANCELLED`() = runTest {
        coEvery { dao.updateStatus(any(), any()) } just Runs

        repository.updateStatus("booking-001", BookingStatus.CANCELLED)
        advanceUntilIdle()

        coVerify { dao.updateStatus("booking-001", "CANCELLED") }
    }

    // ========== Tests de cancel ==========

    @Test
    fun `cancel delegates to updateStatus with CANCELLED`() = runTest {
        coEvery { dao.updateStatus(any(), any()) } just Runs

        repository.cancel("booking-001")
        advanceUntilIdle()

        coVerify { dao.updateStatus("booking-001", "CANCELLED") }
    }

    // ========== Tests de delete ==========

    @Test
    fun `delete calls dao deleteById`() = runTest {
        coEvery { dao.deleteById(any()) } just Runs

        repository.delete("booking-001")
        advanceUntilIdle()

        coVerify(exactly = 1) { dao.deleteById("booking-001") }
    }

    // ========== Tests de deleteAll ==========

    @Test
    fun `deleteAll calls dao deleteAll`() = runTest {
        coEvery { dao.deleteAll() } just Runs

        repository.deleteAll()
        advanceUntilIdle()

        coVerify(exactly = 1) { dao.deleteAll() }
    }

    // ========== Tests de getCount ==========

    @Test
    fun `getCount returns dao count`() = runTest {
        coEvery { dao.getCount() } returns 5

        val count = repository.getCount()

        assertEquals(5, count)
    }

    @Test
    fun `getCount returns 0 when dao throws`() = runTest {
        coEvery { dao.getCount() } throws RuntimeException("Error")

        val count = repository.getCount()

        assertEquals(0, count)
    }

    // ========== Tests de conversión Entity-Model ==========

    @Test
    fun `entity toBooking maps all fields correctly`() {
        val booking = sampleEntity.toBooking()

        assertEquals("booking-001", booking.id)
        assertEquals("gasfiter", booking.serviceId)
        assertEquals("Gasfíter", booking.serviceName)
        assertEquals("🔧", booking.serviceIcon)
        assertEquals("María González", booking.customerName)
        assertEquals(BookingStatus.PENDING, booking.status)
        assertEquals(2000L, booking.createdAt)
    }

    @Test
    fun `entity toBooking handles invalid status gracefully`() {
        val invalidEntity = sampleEntity.copy(status = "INVALID_STATUS")
        val booking = invalidEntity.toBooking()

        // Debe defaultear a PENDING cuando el status es inválido
        assertEquals(BookingStatus.PENDING, booking.status)
    }

    @Test
    fun `booking toEntity maps all fields correctly`() {
        val entity = sampleBooking.toEntity()

        assertEquals("booking-001", entity.id)
        assertEquals("gasfiter", entity.serviceId)
        assertEquals("Gasfíter", entity.serviceName)
        assertEquals("PENDING", entity.status)
        assertEquals(2000L, entity.createdAt)
    }
}

