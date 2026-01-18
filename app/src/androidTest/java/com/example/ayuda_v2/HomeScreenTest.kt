package com.example.ayuda_v2

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addService_then_openDetail() {
        // Espera breve para que la UI se renderice
        Thread.sleep(1000)

        // Abrir dialog desde FAB
        val fab = composeTestRule.onNodeWithText("+")
        fab.assertIsDisplayed()
        fab.performClick()

        // Ingresar datos en el dialog
        val titleInput = composeTestRule.onNodeWithTag("dialog_title_input")
        titleInput.performTextInput("Servicio de prueba")

        val subtitleInput = composeTestRule.onNodeWithTag("dialog_subtitle_input")
        subtitleInput.performTextInput("Técnico disponible")

        // Pulsar Guardar
        val saveButton = composeTestRule.onNodeWithText("Guardar")
        saveButton.performClick()

        // Esperar que el item aparezca en la lista
        val newItem = composeTestRule.onNodeWithText("Servicio de prueba")
        newItem.assertIsDisplayed()

        // Abrir detalle
        newItem.performClick()

        // Verificar detalle
        composeTestRule.onNodeWithTag("detail_id_text").assertIsDisplayed()
    }
}
