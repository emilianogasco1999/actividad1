package com.example.myapplication.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Categoria
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio
import com.example.myapplication.model.ValidadorRecordatorio
import com.example.myapplication.repository.RecordatorioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica y estado del formulario de creación de recordatorios.
 * Recibe eventos de la interfaz, aplica reglas de validación y actualiza el estado mediante
 * instancias inmutables.
 */
class RecordatorioCreateViewModel(
    val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordatorioCreateUiState())
    val uiState: StateFlow<RecordatorioCreateUiState> = _uiState.asStateFlow()

    fun onTituloChange(nuevoTitulo: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(titulo = nuevoTitulo)
            validarYActualizar(newState)
        }
    }

    fun onDescripcionChange(nuevaDescripcion: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(descripcion = nuevaDescripcion)
            validarYActualizar(newState)
        }
    }

    fun onCategoriaSelect(categoria: Categoria) {
        _uiState.update { currentState ->
            val newState = currentState.copy(categoriaSeleccionada = categoria)
            validarYActualizar(newState)
        }
    }

    fun onPrioridadSelect(prioridad: Prioridad) {
        _uiState.update { currentState ->
            val newState = currentState.copy(prioridadSeleccionada = prioridad)
            validarYActualizar(newState)
        }
    }

    fun onUbicacionChange(nuevaUbicacion: String) {
        _uiState.update { currentState ->
            currentState.copy(ubicacionOAviso = nuevaUbicacion)
        }
    }

    fun onFechaLimiteChange(nuevaFecha: String) {
        _uiState.update { currentState ->
            currentState.copy(fechaLimite = nuevaFecha)
        }
    }

    /**
     * Aplica las reglas de validación y retorna el nuevo estado inmutable.
     */
    private fun validarYActualizar(state: RecordatorioCreateUiState): RecordatorioCreateUiState {
        val resultadoVal = ValidadorRecordatorio.validarFormulario(
            titulo = state.titulo,
            descripcion = state.descripcion,
            categoria = state.categoriaSeleccionada,
            prioridad = state.prioridadSeleccionada
        )

        return state.copy(
            errorTitulo = resultadoVal.errorTitulo,
            errorDescripcion = resultadoVal.errorDescripcion,
            errorCategoria = resultadoVal.errorCategoria,
            errorPrioridad = resultadoVal.errorPrioridad,
            isFormValido = resultadoVal.esValido
        )
    }

    /**
     * Ejecuta el guardado del recordatorio de manera asincrónica en [viewModelScope].
     */
    fun guardarRecordatorio() {
        val currentState = _uiState.value

        // Re-validar antes de guardar por seguridad
        val estadoValidado = validarYActualizar(currentState)
        if (!estadoValidado.isFormValido || currentState.isEnviando) {
            return
        }

        val nuevoRecordatorio = Recordatorio(
            id = "REC-" + (100..999).random(),
            titulo = currentState.titulo.trim(),
            descripcion = currentState.descripcion.trim().ifEmpty { null },
            fechaLimite = currentState.fechaLimite.trim().ifEmpty { null },
            prioridad = currentState.prioridadSeleccionada!!,
            categoria = currentState.categoriaSeleccionada!!,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOAviso = currentState.ubicacionOAviso.trim().ifEmpty { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isEnviando = true, errorGeneral = null) }

            val result = repository.guardarRecordatorio(nuevoRecordatorio)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isEnviando = false,
                        exitoEnvio = true,
                        errorGeneral = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isEnviando = false,
                        exitoEnvio = false,
                        errorGeneral = result.exceptionOrNull()?.message ?: "Error al guardar el recordatorio."
                    )
                }
            }
        }
    }

    fun resetExitoEnvio() {
        _uiState.update { it.copy(exitoEnvio = false) }
    }
}
