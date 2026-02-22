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
import com.example.ayuda_v2.data.model.Service
import com.example.ayuda_v2.data.model.ServiceCategory
import com.example.ayuda_v2.data.model.BookingStatus
import com.example.ayuda_v2.viewmodel.BookingViewModel

/**
 * Pantalla principal que muestra los servicios predefinidos.
 * El usuario selecciona un servicio para agendar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onServiceClick: (String) -> Unit,
    onMyBookingsClick: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val services = viewModel.services.collectAsState().value
    val bookings = viewModel.bookings.collectAsState().value
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }

    // Contar reservas pendientes para mostrar badge
    val pendingBookingsCount = bookings.count {
        it.status == BookingStatus.PENDING || it.status == BookingStatus.CONFIRMED
    }

    val filteredServices = if (selectedCategory != null) {
        services.filter { it.category == selectedCategory }
    } else {
        services
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "¿Necesitas ayuda?",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (pendingBookingsCount > 0) {
                                Badge { Text(pendingBookingsCount.toString()) }
                            }
                        }
                    ) {
                        TextButton(onClick = onMyBookingsClick) {
                            Text("Mis Reservas", modifier = Modifier.testTag("btn_my_bookings"))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros por categoría
            CategoryFilter(
                categories = ServiceCategory.entries,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = if (selectedCategory == category) null else category
                }
            )

            // Lista de servicios
            if (filteredServices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay servicios en esta categoría",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredServices, key = { it.id }) { service ->
                        ServiceCard(
                            service = service,
                            onClick = { onServiceClick(service.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilter(
    categories: List<ServiceCategory>,
    selectedCategory: ServiceCategory?,
    onCategorySelected: (ServiceCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_card_${service.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono del servicio
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = service.icon,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del servicio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                service.estimatedPrice?.let { price ->
                    Text(
                        text = price,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Flecha indicadora
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

