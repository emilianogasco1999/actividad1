package com.example.myapplication.ui.create

import com.example.myapplication.model.Categoria
import com.example.myapplication.model.Prioridad

/**
 * Estado inmutable del formulario de creación de un recordatorio (Consigna 3).
 */
data class RecordatorioCreateUiState(
    // Valores actuales de los campos
    val titulo: String = "",
    val descripcion: String = "",
    val categoriaSeleccionada: Categoria? = null,
    val prioridadSeleccionada: Prioridad? = null,
    val ubicacionOAviso: String = "",
    val fechaLimite: String = "",

    // Mensajes de validaciones por campo
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null,

    // Estados de envío y resultado
    val isFormValido: Boolean = false,
    val isEnviando: Boolean = false,
    val exitoEnvio: Boolean = false,
    val errorGeneral: String? = null
)
