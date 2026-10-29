package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.repository.InMemoryRecordatorioRepository
import com.example.myapplication.repository.RecordatorioRepository
import com.example.myapplication.ui.create.RecordatorioCreateScreen
import com.example.myapplication.ui.create.RecordatorioCreateViewModel
import com.example.myapplication.ui.detail.RecordatorioDetailScreen
import com.example.myapplication.ui.detail.RecordatorioDetailViewModel
import com.example.myapplication.ui.list.RecordatorioListScreen
import com.example.myapplication.ui.list.RecordatorioListViewModel

/**
 * Rutas de la aplicación para Navigation Compose (Consigna 5).
 */
object AppDestinations {
    const val LISTA = "recordatorio_list"
    const val CREAR = "recordatorio_create"
    const val DETALLE = "recordatorio_detail/{recordatorioId}"

    fun detalleRuta(recordatorioId: String) = "recordatorio_detail/$recordatorioId"
}

/**
 * Grafo de navegación centralizado con NavHost (Consigna 5).
 * Administra el [NavHostController] y desacopla la navegación de las pantallas individuales
 * mediante lambdas como onCrearRecordatorio, onRecordatorioClick, onGuardarExito, etc.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    repository: RecordatorioRepository = remember { InMemoryRecordatorioRepository() }
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.LISTA
    ) {
        // Destino 1: Listado de Recordatorios
        composable(route = AppDestinations.LISTA) {
            val listViewModel = remember(repository) {
                RecordatorioListViewModel(repository)
            }

            RecordatorioListScreen(
                onCrearRecordatorio = {
                    navController.navigate(AppDestinations.CREAR)
                },
                onRecordatorioClick = { recordatorioId ->
                    navController.navigate(AppDestinations.detalleRuta(recordatorioId))
                },
                viewModel = listViewModel
            )
        }

        // Destino 2: Formulario de Creación de Recordatorio
        composable(route = AppDestinations.CREAR) {
            val createViewModel = remember(repository) {
                RecordatorioCreateViewModel(repository)
            }

            RecordatorioCreateScreen(
                onGuardarExito = {
                    // Volver al listado sin agregar copias duplicadas al historial (Consigna 5)
                    navController.popBackStack()
                },
                onCancelar = {
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                },
                viewModel = createViewModel
            )
        }

        // Destino 3: Detalle del Recordatorio (recibe únicamente el id)
        composable(
            route = AppDestinations.DETALLE,
            arguments = listOf(
                navArgument("recordatorioId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recordatorioId = backStackEntry.arguments?.getString("recordatorioId") ?: ""

            val detailViewModel = remember(recordatorioId, repository) {
                RecordatorioDetailViewModel(
                    recordatorioId = recordatorioId,
                    repository = repository
                )
            }

            RecordatorioDetailScreen(
                onVolver = {
                    navController.popBackStack()
                },
                viewModel = detailViewModel
            )
        }
    }
}
