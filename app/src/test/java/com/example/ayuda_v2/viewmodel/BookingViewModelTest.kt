package com.example.ayuda_v2.viewmodel

import com.example.ayuda_v2.data.IBookingRepository
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus
import com.example.ayuda_v2.ui.state.UiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para BookingViewModel.
 * Verifica la lógica de negocio: creación, cancelación, completación de reservas
 * y manejo de errores.
 *
 * Usa MockK para mockear IBookingRepository y coroutines-test para controlar dispatchers.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    private lateinit var repository: IBookingRepository
    private lateinit var viewModel: BookingViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleBooking = Booking(
        id = "test-id-1",
        serviceId = "electricista",
        serviceName = "Electricista",
        serviceIcon = "⚡",
        customerName = "Juan Pérez",
        customerPhone = "+56912345678",
        customerAddress = "Av. Siempre Viva 742",
        scheduledDate = "2026-03-15",
        scheduledTime = "10:00",
        notes = "Timbre no funciona",
        status = BookingStatus.PENDING,
        createdAt = 1000L
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

        repository = mockk(relaxed = true)
        // Default: flow emits empty list
        every { repository.getAllBookingsFlow() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    private fun createViewModel(): BookingViewModel {
        return BookingViewModel(repository)
    }

    // ========== Tests de carga de servicios ==========

    @Test
    fun `services list contains 12 predefined services`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(12, viewModel.services.value.size)
    }

    @Test
    fun `services list contains electricista`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        val service = viewModel.getServiceById("electricista")
        assertNotNull(service)
        assertEquals("Electricista", service?.name)
    }

    @Test
    fun `getServiceById returns null for unknown id`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        val service = viewModel.getServiceById("servicio_inexistente")
        assertNull(service)
    }

    @Test
    fun `services include all categories`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()
        val categories = viewModel.services.value.map { it.category }.distinct()
        assertEquals(4, categories.size)
    }

    // ========== Tests de creación de reservas ==========

    @Test
    fun `createBooking calls repository add on success`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.add(any()) } just Runs

        viewModel.createBooking(
            serviceId = "electricista",
            customerName = "Juan Pérez",
            customerPhone = "+56912345678",
            customerAddress = "Calle 123",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00",
            notes = "Test"
        )
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.add(match { it.serviceId == "electricista" }) }
    }

    @Test
    fun `createBooking sets Success state on valid input`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.add(any()) } just Runs

        viewModel.createBooking(
            serviceId = "electricista",
            customerName = "Juan Pérez",
            customerPhone = "+56912345678",
            customerAddress = "Calle 123",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `createBooking sets Error state for invalid service id`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.createBooking(
            serviceId = "servicio_fantasma",
            customerName = "Juan",
            customerPhone = "123",
            customerAddress = "Calle",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected UiState.Error but got $state", state is UiState.Error)
        assertTrue((state as UiState.Error).message.contains("Servicio no encontrado"))
    }

    @Test
    fun `createBooking sets Error state when repository throws`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.add(any()) } throws RuntimeException("DB error")

        viewModel.createBooking(
            serviceId = "electricista",
            customerName = "Juan",
            customerPhone = "123",
            customerAddress = "Calle",
            scheduledDate = "2026-03-15",
            scheduledTime = "10:00"
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
    }

    @Test
    fun `createBooking preserves customer data in booking`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val capturedBooking = slot<Booking>()
        coEvery { repository.add(capture(capturedBooking)) } just Runs

        viewModel.createBooking(
            serviceId = "gasfiter",
            customerName = "María López",
            customerPhone = "+56911111111",
            customerAddress = "Av. Principal 100",
            scheduledDate = "2026-05-20",
            scheduledTime = "15:30",
            notes = "Urgente"
        )
        advanceUntilIdle()

        assertEquals("María López", capturedBooking.captured.customerName)
        assertEquals("+56911111111", capturedBooking.captured.customerPhone)
        assertEquals("Av. Principal 100", capturedBooking.captured.customerAddress)
        assertEquals("2026-05-20", capturedBooking.captured.scheduledDate)
        assertEquals("15:30", capturedBooking.captured.scheduledTime)
        assertEquals("Urgente", capturedBooking.captured.notes)
        assertEquals(BookingStatus.PENDING, capturedBooking.captured.status)
    }

    // ========== Tests de cancelación ==========

    @Test
    fun `cancelBooking calls repository cancel`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.cancel(any()) } just Runs

        viewModel.cancelBooking("test-id-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.cancel("test-id-1") }
    }

    @Test
    fun `cancelBooking sets Success state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.cancel(any()) } just Runs

        viewModel.cancelBooking("test-id-1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    // ========== Tests de completar reserva ==========

    @Test
    fun `completeBooking calls repository updateStatus with COMPLETED`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.updateStatus(any(), any()) } just Runs

        viewModel.completeBooking("test-id-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.updateStatus("test-id-1", BookingStatus.COMPLETED) }
    }

    @Test
    fun `completeBooking sets Success state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.updateStatus(any(), any()) } just Runs

        viewModel.completeBooking("test-id-1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    // ========== Tests de eliminación ==========

    @Test
    fun `deleteBooking calls repository delete`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.delete(any()) } just Runs

        viewModel.deleteBooking("test-id-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.delete("test-id-1") }
    }

    // ========== Tests de estado de UI ==========

    @Test
    fun `resetUiState sets state to Idle`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.resetUiState()
        assertTrue(viewModel.uiState.value is UiState.Idle)
    }

    @Test
    fun `clearAllBookings calls repository deleteAll`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { repository.deleteAll() } just Runs

        viewModel.clearAllBookings()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteAll() }
        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    // ========== Tests de observación del Flow ==========

    @Test
    fun `bookings flow updates when repository emits new data`() = runTest {
        val bookingsList = listOf(sampleBooking)
        every { repository.getAllBookingsFlow() } returns flowOf(bookingsList)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.bookings.value.size)
        assertEquals("test-id-1", viewModel.bookings.value.first().id)
    }

    @Test
    fun `initial bookings list is empty`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.bookings.value.isEmpty())
    }
}

