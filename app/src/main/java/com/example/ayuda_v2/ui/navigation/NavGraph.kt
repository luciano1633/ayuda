package com.example.ayuda_v2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ayuda_v2.AyudaApplication
import com.example.ayuda_v2.ui.screens.ServicesScreen
import com.example.ayuda_v2.ui.screens.BookingFormScreen
import com.example.ayuda_v2.ui.screens.MyBookingsScreen
import com.example.ayuda_v2.viewmodel.BookingViewModel
import com.example.ayuda_v2.viewmodel.BookingViewModelFactory

/**
 * Destinos de navegación de la aplicación.
 */
object Destinations {
    const val SERVICES = "services"
    const val BOOKING_FORM = "booking/{serviceId}"
    const val MY_BOOKINGS = "my_bookings"

    fun bookingForm(serviceId: String) = "booking/$serviceId"
}

/**
 * Grafo de navegación principal.
 * Utiliza Navigation Compose para gestionar las transiciones entre pantallas.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    // Obtener el repositorio desde Application para inyección de dependencias
    val context = LocalContext.current
    val application = context.applicationContext as AyudaApplication
    val factory = BookingViewModelFactory(application.bookingRepository)

    // ViewModel compartido para todas las pantallas
    val bookingViewModel: BookingViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = Destinations.SERVICES) {
        // Pantalla principal - Lista de servicios predefinidos
        composable(Destinations.SERVICES) {
            ServicesScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(Destinations.bookingForm(serviceId))
                },
                onMyBookingsClick = {
                    navController.navigate(Destinations.MY_BOOKINGS)
                },
                viewModel = bookingViewModel
            )
        }

        // Pantalla de formulario de reserva
        composable(Destinations.BOOKING_FORM) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            BookingFormScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onBookingCreated = {
                    navController.popBackStack(Destinations.SERVICES, inclusive = false)
                },
                viewModel = bookingViewModel
            )
        }

        // Pantalla de mis reservas
        composable(Destinations.MY_BOOKINGS) {
            MyBookingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = bookingViewModel
            )
        }
    }
}
