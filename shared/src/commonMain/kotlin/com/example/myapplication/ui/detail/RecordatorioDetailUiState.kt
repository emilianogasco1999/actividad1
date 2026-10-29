package com.example.myapplication.ui.detail

import com.example.myapplication.model.CategoriaConfig
import com.example.myapplication.model.Recordatorio

/**
 * Estado inmutable de la pantalla de detalle de un recordatorio (Consigna 3).
 */
sealed interface RecordatorioDetailUiState {
    data object Carga : RecordatorioDetailUiState
    data class ConDatos(
        val recordatorio: Recordatorio,
        val configCategoria: CategoriaConfig
    ) : RecordatorioDetailUiState
    data class Error(val mensaje: String) : RecordatorioDetailUiState
}
