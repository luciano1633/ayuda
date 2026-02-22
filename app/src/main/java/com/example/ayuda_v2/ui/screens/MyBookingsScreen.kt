package com.example.ayuda_v2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ayuda_v2.data.model.Booking
import com.example.ayuda_v2.data.model.BookingStatus
import com.example.ayuda_v2.ui.state.UiState
import com.example.ayuda_v2.viewmodel.BookingViewModel

/**
 * Pantalla que muestra las reservas del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val bookings = viewModel.bookings.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    var bookingToCancel by remember { mutableStateOf<String?>(null) }
    var bookingToComplete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar(uiState.message)
            viewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas") },
                navigationIcon = {
                    Text(
                        text = "←",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clickable { onBack() }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState is UiState.Loading && bookings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                bookings.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📋",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes reservas aún",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Selecciona un servicio para hacer tu primera reserva",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookings, key = { it.id }) { booking ->
                            BookingCard(
                                booking = booking,
                                onCancel = { bookingToCancel = booking.id },
                                onComplete = { bookingToComplete = booking.id }
                            )
                        }
                    }
                }
            }

            // Loading overlay
            if (uiState is UiState.Loading && bookings.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Cancel confirmation dialog
        bookingToCancel?.let { bookingId ->
            AlertDialog(
                onDismissRequest = { bookingToCancel = null },
                title = { Text("Cancelar Reserva") },
                text = { Text("¿Estás seguro de que deseas cancelar esta reserva?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.cancelBooking(bookingId)
                            bookingToCancel = null
                        }
                    ) {
                        Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingToCancel = null }) {
                        Text("No")
                    }
                }
            )
        }

        // Complete confirmation dialog
        bookingToComplete?.let { bookingId ->
            AlertDialog(
                onDismissRequest = { bookingToComplete = null },
                title = { Text("Marcar como Realizado") },
                text = { Text("¿Confirmas que el servicio ya fue realizado?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.completeBooking(bookingId)
                            bookingToComplete = null
                        }
                    ) {
                        Text("Sí, realizado", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingToComplete = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("booking_card_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with icon and service name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.serviceIcon,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.serviceName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    StatusChip(status = booking.status)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Details
            BookingDetailRow(label = "📅 Fecha:", value = booking.scheduledDate)
            BookingDetailRow(label = "🕐 Hora:", value = booking.scheduledTime)
            BookingDetailRow(label = "📍 Dirección:", value = booking.customerAddress)

            if (booking.notes.isNotBlank()) {
                BookingDetailRow(label = "📝 Notas:", value = booking.notes)
            }

            // Action buttons (only for pending/confirmed bookings)
            if (booking.status == BookingStatus.PENDING || booking.status == BookingStatus.CONFIRMED) {
                Spacer(modifier = Modifier.height(12.dp))

                // Complete button
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().testTag("btn_complete_${booking.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("✓ Marcar como Realizado")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cancel button
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().testTag("btn_cancel_${booking.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar Reserva")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: BookingStatus) {
    val (color, backgroundColor) = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.COMPLETED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


