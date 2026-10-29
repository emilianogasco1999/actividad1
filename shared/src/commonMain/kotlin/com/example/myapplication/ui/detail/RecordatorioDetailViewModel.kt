package com.example.myapplication.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.RecordatorioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de buscar y cargar la información completa de un recordatorio por su ID.
 */
class RecordatorioDetailViewModel(
    val recordatorioId: String,
    val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordatorioDetailUiState>(RecordatorioDetailUiState.Carga)
    val uiState: StateFlow<RecordatorioDetailUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.value = RecordatorioDetailUiState.Carga
            
            delay(400L) // Simulación de carga asincrónica

            repository.obtenerRecordatorioPorId(recordatorioId)
                .catch { exception ->
                    _uiState.value = RecordatorioDetailUiState.Error(
                        exception.message ?: "Ocurrió un error al cargar el detalle del recordatorio."
                    )
                }
                .collect { recordatorio ->
                    if (recordatorio != null) {
                        val config = repository.obtenerConfiguracionCategoria(recordatorio.categoria)
                        _uiState.value = RecordatorioDetailUiState.ConDatos(
                            recordatorio = recordatorio,
                            configCategoria = config
                        )
                    } else {
                        _uiState.value = RecordatorioDetailUiState.Error(
                            "No se encontró ningún recordatorio con el identificador: $recordatorioId"
                        )
                    }
                }
        }
    }
}
