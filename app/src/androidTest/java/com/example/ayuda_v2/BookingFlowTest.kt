package com.example.ayuda_v2

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas funcionales para el flujo de reservas.
 * Verifica la navegación entre pantallas y la interacción del usuario
 * usando Compose Testing Framework (equivalente a Espresso para Compose).
 */
@RunWith(AndroidJUnit4::class)
class BookingFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
    fun servicesScreen_filterByCategory() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tecnología").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Técnico PC").assertIsDisplayed()
        composeTestRule.onNodeWithText("Técnico Celular").assertIsDisplayed()
    }

    @Test
    fun servicesScreen_clickService_navigatesToBookingForm() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("service_card_electricista").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Agendar Servicio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electricista").assertIsDisplayed()
    }

    @Test
    fun bookingForm_displaysServiceInfo() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("service_card_gasfiter").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Gasfíter").assertIsDisplayed()
    }

    @Test
    fun bookingForm_displaysFormFields() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("service_card_electricista").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Nombre completo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Teléfono").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dirección").assertIsDisplayed()
    }

    @Test
    fun bookingForm_backButton_returnsToServices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("service_card_electricista").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }

    @Test
    fun myBookings_navigation_works() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        // Title should be displayed
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
    fun myBookings_backButton_returnsToServices() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mis Reservas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("←").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("¿Necesitas ayuda?").assertIsDisplayed()
    }
}

