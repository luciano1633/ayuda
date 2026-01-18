package com.example.ayuda_v2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ayuda_v2.ui.screens.DetailScreen
import com.example.ayuda_v2.ui.screens.HomeScreen
import com.example.ayuda_v2.viewmodel.ServicesViewModel

object Destinations {
    const val HOME = "home"
    const val DETAIL = "detail/{id}"
    const val DETAIL_ROUTE = "detail"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val servicesViewModel: ServicesViewModel = viewModel()
    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(onItemClick = { id ->
                navController.navigate("${Destinations.DETAIL_ROUTE}/$id")
            }, viewModel = servicesViewModel)
        }

        composable("${Destinations.DETAIL_ROUTE}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "-"
            DetailScreen(id = id, onBack = { navController.popBackStack() }, servicesViewModel = servicesViewModel)
        }
    }
}
