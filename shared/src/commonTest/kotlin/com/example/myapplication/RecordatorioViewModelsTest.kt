package com.example.myapplication

import com.example.myapplication.model.Prioridad
import com.example.myapplication.repository.InMemoryRecordatorioRepository
import com.example.myapplication.ui.create.RecordatorioCreateViewModel
import com.example.myapplication.ui.detail.RecordatorioDetailUiState
import com.example.myapplication.ui.detail.RecordatorioDetailViewModel
import com.example.myapplication.ui.list.RecordatorioListUiState
import com.example.myapplication.ui.list.RecordatorioListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecordatorioViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testListViewModelCargaEstadoConDatos() = runTest {
        val repository = InMemoryRecordatorioRepository()
        val viewModel = RecordatorioListViewModel(repository)

        // Inicialmente en Carga
        assertEquals(RecordatorioListUiState.Carga, viewModel.uiState.value)

        // Avanzar el reloj de corrutinas para resolver el delay simulado
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is RecordatorioListUiState.ConDatos)
        assertTrue(state.recordatorios.isNotEmpty())
    }

    @Test
    fun testCreateViewModelValidacionYGuardado() = runTest {
        val repository = InMemoryRecordatorioRepository()
        val viewModel = RecordatorioCreateViewModel(repository)

        // Inicialmente inválido (campos vacíos)
        assertFalse(viewModel.uiState.value.isFormValido)

        // Ingresar título inválido (menos de 4 caracteres)
        viewModel.onTituloChange("abc")
        assertNotNull(viewModel.uiState.value.errorTitulo)
        assertFalse(viewModel.uiState.value.isFormValido)

        // Ingresar datos completos y válidos
        viewModel.onTituloChange("Reunión Importante")
        viewModel.onDescripcionChange("Detalles de la reunión")
        viewModel.onCategoriaSelect(repository.catTrabajo)
        viewModel.onPrioridadSelect(Prioridad.ALTA)

        // Ahora el formulario debe ser válido
        assertNull(viewModel.uiState.value.errorTitulo)
        assertNull(viewModel.uiState.value.errorCategoria)
        assertNull(viewModel.uiState.value.errorPrioridad)
        assertTrue(viewModel.uiState.value.isFormValido)

        // Ejecutar guardado
        viewModel.guardarRecordatorio()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.exitoEnvio)
    }

    @Test
    fun testDetailViewModelCargaExitosaPorId() = runTest {
        val repository = InMemoryRecordatorioRepository()
        val viewModel = RecordatorioDetailViewModel("REC-001", repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is RecordatorioDetailUiState.ConDatos)
        assertEquals("REC-001", state.recordatorio.id)
    }
}
