package com.example.ayuda_v2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ayuda_v2.ui.components.ConfirmDialog
import com.example.ayuda_v2.ui.components.AddEditServiceDialog
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ayuda_v2.viewmodel.ServicesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(id: String, onBack: () -> Unit, servicesViewModel: ServicesViewModel = viewModel()) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val showDeleteConfirm = remember { mutableStateOf(false) }
    val items = servicesViewModel.items.collectAsState().value

    val item = items.find { it.id == id }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Detalle del servicio", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable { onBack() }
                        .semantics { contentDescription = "Volver" }
                )
            }
        )

        Surface(modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp), tonalElevation = 2.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ID: ${item?.id ?: id}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag("detail_id_text")
                        .semantics { contentDescription = "ID del servicio: ${item?.id ?: id}" }
                )

                Text(text = item?.title ?: "(sin título)", style = MaterialTheme.typography.titleMedium)
                Text(text = item?.subtitle ?: "", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 12.dp))

                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.semantics { contentDescription = "Editar servicio" },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(text = "Editar")
                }

                Button(
                    onClick = { showDeleteConfirm.value = true },
                    modifier = Modifier.semantics { contentDescription = "Eliminar servicio" },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(text = "Eliminar")
                }

                if (showDialog.value) {
                    AddEditServiceDialog(initialTitle = item?.title ?: "", initialSubtitle = item?.subtitle ?: "", onConfirm = { newTitle, newSubtitle ->
                        servicesViewModel.update(id = item?.id ?: id, title = newTitle, subtitle = newSubtitle)
                        showDialog.value = false
                    }, onDismiss = { showDialog.value = false })
                }

                if (showDeleteConfirm.value) {
                    ConfirmDialog(title = "Eliminar servicio", text = "Confirma eliminar el servicio?", onConfirm = {
                        servicesViewModel.delete(id = item?.id ?: id)
                        showDeleteConfirm.value = false
                        onBack()
                    }, onDismiss = { showDeleteConfirm.value = false })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    DetailScreen(id = "1", onBack = {}, servicesViewModel = ServicesViewModel(android.app.Application()))
}
