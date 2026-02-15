package com.example.ayuda_v2.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ayuda_v2.ui.components.HelpItem
import com.example.ayuda_v2.ui.components.AddEditServiceDialog
import com.example.ayuda_v2.ui.state.UiState
import com.example.ayuda_v2.viewmodel.ServicesViewModel

private val sampleData = List(8) { index -> HelpModel(id = index.toString(), title = "Servicio #${index + 1}", subtitle = "Técnico disponible") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onItemClick: (String) -> Unit, viewModel: ServicesViewModel = viewModel()) {
    val showDialog = remember { mutableStateOf(false) }
    val items = viewModel.items.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error messages in snackbar
    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar(uiState.message)
            viewModel.resetUiState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text(text = "¿Necesitas ayuda?", style = MaterialTheme.typography.titleLarge) })

            Surface(modifier = Modifier.padding(12.dp).weight(1f), color = MaterialTheme.colorScheme.surface) {
                when {
                    uiState is UiState.Loading && items.isEmpty() -> {
                        // Show loading indicator when initially loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    items.isEmpty() -> {
                        // Show empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay servicios disponibles.\nPresiona + para agregar uno.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.padding(4.dp)) {
                            items(items, key = { it.id }) { item ->
                                HelpItem(item = item, onClick = { onItemClick(item.id) })
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showDialog.value = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "+", style = MaterialTheme.typography.titleLarge)
            }
        }

        // Snackbar for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Loading overlay for operations
        if (uiState is UiState.Loading && items.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showDialog.value) {
        AddEditServiceDialog(onConfirm = { title, subtitle ->
            viewModel.add(title, subtitle)
            showDialog.value = false
        }, onDismiss = {
            showDialog.value = false
        })
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onItemClick = {}, viewModel = ServicesViewModel(android.app.Application()))
}

/**
 * Data class representing a service/help item.
 * @param id Unique identifier
 * @param title Service title
 * @param subtitle Service description
 * @param imageUrl Optional URL for service image (loaded via Glide)
 */
data class HelpModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null // Optional image URL for Glide loading
)
