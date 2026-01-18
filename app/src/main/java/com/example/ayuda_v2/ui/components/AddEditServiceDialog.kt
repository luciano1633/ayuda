package com.example.ayuda_v2.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Text as MText
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag

@Composable
fun AddEditServiceDialog(
    initialTitle: String = "",
    initialSubtitle: String = "",
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val title = remember { mutableStateOf(initialTitle) }
    val subtitle = remember { mutableStateOf(initialSubtitle) }
    val titleError = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (initialTitle.isEmpty()) "Agregar servicio" else "Editar servicio") },
        text = {
            Column {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = {
                        title.value = it
                        titleError.value = if (it.isBlank()) "El título no puede estar vacío" else ""
                    },
                    label = { Text("Título") },
                    isError = titleError.value.isNotEmpty(),
                    modifier = Modifier.testTag("dialog_title_input")
                )
                if (titleError.value.isNotEmpty()) {
                    MText(text = titleError.value, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                }
                OutlinedTextField(
                    value = subtitle.value,
                    onValueChange = { subtitle.value = it },
                    label = { Text("Subtítulo") },
                    modifier = Modifier.testTag("dialog_subtitle_input")
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.value.isNotBlank()) onConfirm(title.value.trim(), subtitle.value.trim())
            }, enabled = title.value.isNotBlank()) { Text(text = "Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancelar") }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}
