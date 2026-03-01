package com.example.ayuda_v2

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ayuda_v2.ui.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas funcionales para el flujo de reservas.
 * Verifica la navegación entre pantallas y la interacción del usuario
 * usando Compose Testing Framework (equivalente a Espresso para Compose).
 *
 * Usa TestTags centralizados para consistencia y mantenibilidad.
 */
@RunWith(AndroidJUnit4::class)
class BookingFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ========== Tests de pantalla principal (ServicesScreen) ==========

    @Test
    fun servicesScreen_displaysTitle() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_displaysServiceCategories() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Hogar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tecnología").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vehículos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salud").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_displaysPredefinedServices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Electricista").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gasfíter").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_displaysServicePrices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Desde \$25.000").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_filterByCategory() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tecnología").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Técnico PC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Técnico Celular").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_filterByCategory_showsVehiculos() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Vehículos").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mecánico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grúa").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_filterByCategory_showsSalud() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Salud").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Enfermera a Domicilio").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_toggleFilter_showsAll() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tecnología").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tecnología").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Electricista").assertIsDisplayed()
    }

    // ========== Tests de navegación a formulario ==========

    @Test
    fun servicesScreen_clickService_navigatesToBookingForm() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Agendar Servicio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electricista").assertIsDisplayed()
    }

    @Test
    fun bookingForm_displaysServiceInfo() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("gasfiter")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Gasfíter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reparación de cañerías y grifería").assertIsDisplayed()
    }

    @Test
    fun bookingForm_displaysFormFields() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Nombre completo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teléfono").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dirección").assertIsDisplayed()
    }

    @Test
    fun bookingForm_confirmButton_disabledWhenEmpty() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.BTN_CONFIRM_BOOKING).assertIsNotEnabled()
    }

    @Test
    fun bookingForm_canInputCustomerData() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.INPUT_NAME).performTextInput("Juan Pérez")
        composeTestRule.onNodeWithTag(TestTags.INPUT_PHONE).performTextInput("56912345678")
        composeTestRule.onNodeWithTag(TestTags.INPUT_ADDRESS).performTextInput("Av. Test 123")

        composeTestRule.onNodeWithText("Juan Pérez").assertIsDisplayed()
    }

    @Test
    fun bookingForm_backButton_returnsToServices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("electricista")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }

    // ========== Tests de Mis Reservas ==========

    @Test
    fun myBookings_navigation_works() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").assertIsDisplayed()
    }

    @Test
    fun myBookings_showsEmptyState_whenNoBookings() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No tienes reservas aún").assertIsDisplayed()
    }

    @Test
    fun myBookings_showsEmptyStateMessage() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Selecciona un servicio para hacer tu primera reserva").assertIsDisplayed()
    }

    @Test
    fun myBookings_backButton_returnsToServices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }

    // ========== Tests de flujo completo ==========

    @Test
    fun fullFlow_navigateToServiceAndBackToHome() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.serviceCard("cerrajero")).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cerrajero").assertIsDisplayed()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }

    @Test
    fun fullFlow_navigateToBookingsAndBack() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No tienes reservas aún").assertIsDisplayed()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }
}
