package com.example.myapplication.ui.list

import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio

/**
 * Representa los estados inmutables y mutuamente excluyentes de la pantalla de listado (Consigna 3).
 */
sealed interface RecordatorioListUiState {
    
    /** Estado mientras se cargan los recordatorios desde el repositorio. */
    data object Carga : RecordatorioListUiState

    /** Estado cuando el repositorio está vacío y no hay registros creados. */
    data object Vacio : RecordatorioListUiState

    /** Estado exitoso con la lista de recordatorios y los filtros aplicados. */
    data class ConDatos(
        val recordatorios: List<Recordatorio>,
        val filtroPrioridad: Prioridad? = null,
        val filtroEstado: EstadoRecordatorio? = null,
        val textoBusqueda: String = ""
    ) : RecordatorioListUiState

    /** Estado de error con mensaje explicativo para el usuario. */
    data class Error(val mensaje: String) : RecordatorioListUiState
}
