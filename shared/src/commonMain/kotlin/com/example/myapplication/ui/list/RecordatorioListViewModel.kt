package com.example.myapplication.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio
import com.example.myapplication.repository.RecordatorioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el estado de la pantalla de listado de recordatorios.
 * Depende de la interfaz [RecordatorioRepository] para cumplir con Inversión de Dependencias (DIP).
 */
class RecordatorioListViewModel(
    val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordatorioListUiState>(RecordatorioListUiState.Carga)
    val uiState: StateFlow<RecordatorioListUiState> = _uiState.asStateFlow()

    // Filtros activos en memoria
    private var textoBusquedaActual = ""
    private var prioridadFiltroActual: Prioridad? = null
    private var estadoFiltroActual: EstadoRecordatorio? = null

    init {
        cargarRecordatorios()
    }

    /**
     * Inicia o reinicia la recolección de recordatorios simulando una operación asincrónica.
     */
    fun cargarRecordatorios() {
        viewModelScope.launch {
            _uiState.value = RecordatorioListUiState.Carga
            
            // Simulación de delay de carga para probar el spinner
            delay(500L)

            if (repository.estaErrorSimuladoActivo()) {
                _uiState.value = RecordatorioListUiState.Error(
                    "Error de conexión al cargar los recordatorios. Verifique la señal o intente nuevamente."
                )
                return@launch
            }

            repository.obtenerRecordatorios()
                .catch { exception ->
                    _uiState.value = RecordatorioListUiState.Error(
                        exception.message ?: "Ocurrió un error inesperado al obtener los datos."
                    )
                }
                .collect { lista ->
                    procesarLista(lista)
                }
        }
    }

    private fun procesarLista(listaCompleta: List<Recordatorio>) {
        if (listaCompleta.isEmpty()) {
            _uiState.value = RecordatorioListUiState.Vacio
            return
        }

        val listaFiltrada = listaCompleta.filter { recordatorio ->
            val coincidePrioridad = prioridadFiltroActual == null || recordatorio.prioridad == prioridadFiltroActual
            val coincideEstado = estadoFiltroActual == null || recordatorio.estado.nombre == estadoFiltroActual?.nombre
            val coincideTexto = textoBusquedaActual.isBlank() ||
                    recordatorio.titulo.contains(textoBusquedaActual, ignoreCase = true) ||
                    (recordatorio.descripcion?.contains(textoBusquedaActual, ignoreCase = true) == true)

            coincidePrioridad && coincideEstado && coincideTexto
        }

        _uiState.value = RecordatorioListUiState.ConDatos(
            recordatorios = listaFiltrada,
            filtroPrioridad = prioridadFiltroActual,
            filtroEstado = estadoFiltroActual,
            textoBusqueda = textoBusquedaActual
        )
    }

    fun actualizarBusqueda(texto: String) {
        textoBusquedaActual = texto
        reprocesarActual()
    }

    fun seleccionarPrioridad(prioridad: Prioridad?) {
        prioridadFiltroActual = if (prioridadFiltroActual == prioridad) null else prioridad
        reprocesarActual()
    }

    fun seleccionarEstado(estado: EstadoRecordatorio?) {
        estadoFiltroActual = if (estadoFiltroActual?.nombre == estado?.nombre) null else estado
        reprocesarActual()
    }

    fun limpiarFiltros() {
        textoBusquedaActual = ""
        prioridadFiltroActual = null
        estadoFiltroActual = null
        reprocesarActual()
    }

    /**
     * Permite probar la simulación del estado de error exigida en la consigna 3.
     */
    fun toggleErrorSimulado() {
        val estaActivo = repository.estaErrorSimuladoActivo()
        repository.simularError(!estaActivo)
        cargarRecordatorios()
    }

    private fun reprocesarActual() {
        viewModelScope.launch {
            repository.obtenerRecordatorios().collect { lista ->
                procesarLista(lista)
            }
        }
    }
}
