package com.example.ayuda_v2.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ayuda_v2.data.model.PredefinedServices
import com.example.ayuda_v2.ui.state.UiState
import com.example.ayuda_v2.viewmodel.BookingViewModel
import java.util.*

/**
 * Pantalla para agendar un servicio.
 * El usuario ingresa sus datos y selecciona fecha/hora.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    serviceId: String,
    onBack: () -> Unit,
    onBookingCreated: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val service = remember { PredefinedServices.getById(serviceId) }
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    // Form fields
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Track if we submitted the form (to know when Success means we should navigate)
    var formSubmitted by remember { mutableStateOf(false) }

    // Validation
    val isFormValid = customerName.isNotBlank() &&
            customerPhone.isNotBlank() &&
            customerAddress.isNotBlank() &&
            selectedDate.isNotBlank() &&
            selectedTime.isNotBlank()

    // Handle UI state changes - only navigate on Success if we submitted the form
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                if (formSubmitted) {
                    snackbarHostState.showSnackbar("¡Reserva creada exitosamente!")
                    viewModel.resetUiState()
                    onBookingCreated()
                }
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
                viewModel.resetUiState()
                formSubmitted = false
            }
            else -> {}
        }
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis()
    }

    // Time picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Servicio") },
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
        if (service == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Servicio no encontrado")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.icon,
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = service.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        service.estimatedPrice?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "Tus datos",
                style = MaterialTheme.typography.titleMedium
            )

            // Name field
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Phone field
            OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it.filter { c -> c.isDigit() || c == '+' } },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                placeholder = { Text("+56 9 1234 5678") }
            )

            // Address field
            OutlinedTextField(
                value = customerAddress,
                onValueChange = { customerAddress = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            HorizontalDivider()

            Text(
                text = "Fecha y hora",
                style = MaterialTheme.typography.titleMedium
            )

            // Date picker
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Fecha", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = if (selectedDate.isNotBlank()) selectedDate else "Seleccionar fecha",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedDate.isNotBlank())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("📅", fontSize = 24.sp)
                }
            }

            // Time picker
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Hora", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = if (selectedTime.isNotBlank()) selectedTime else "Seleccionar hora",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedTime.isNotBlank())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("🕐", fontSize = 24.sp)
                }
            }

            // Notes field (optional)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("Ej: Timbre no funciona, llamar al llegar") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    formSubmitted = true
                    viewModel.createBooking(
                        serviceId = serviceId,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerAddress = customerAddress,
                        scheduledDate = selectedDate,
                        scheduledTime = selectedTime,
                        notes = notes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid && uiState !is UiState.Loading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar Reserva", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



