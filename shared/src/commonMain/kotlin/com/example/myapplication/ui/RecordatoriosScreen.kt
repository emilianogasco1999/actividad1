package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import com.example.myapplication.ui.navigation.AppNavigation

/**
 * Punto de entrada retrocompatible para la interfaz de recordatorios.
 * Redirige al grafo de navegación completo configurado para la Actividad 2.
 */
@Composable
fun RecordatoriosScreen() {
    AppNavigation()
}
