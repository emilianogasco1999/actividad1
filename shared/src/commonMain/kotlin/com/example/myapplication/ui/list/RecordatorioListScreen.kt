package com.example.myapplication.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.components.ErrorStateView
import com.example.myapplication.ui.components.LoadingStateView
import com.example.myapplication.ui.components.RecordatorioItemCard

/**
 * Pantalla de listado de recordatorios que observa el estado expuesto por el [RecordatorioListViewModel]
 * mediante [collectAsStateWithLifecycle] y responde de forma reactiva y guiada por el ciclo de vida (Consignas 1, 3, 4).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioListScreen(
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (String) -> Unit,
    viewModel: RecordatorioListViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Recordatorios Contextuales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Actividad 2 - Listado de Tareas",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    // Botón para simular error y comprobar la consigna 3
                    TextButton(onClick = { viewModel.toggleErrorSimulado() }) {
                        Text(
                            text = if (viewModel.repository.estaErrorSimuladoActivo()) "⚡ Desactivar Error" else "⚡ Simular Error",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearRecordatorio,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Nuevo", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Manejo exhaustivo de los estados mutually exclusive con when (Consigna 3)
            when (val state = uiState) {
                is RecordatorioListUiState.Carga -> {
                    LoadingStateView(
                        mensaje = "Cargando lista de recordatorios...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RecordatorioListUiState.Vacio -> {
                    EmptyStateView(
                        titulo = "No hay recordatorios registrados",
                        descripcion = "Aún no tenés ningún recordatorio guardado. Presioná el botón a continuación para crear tu primer registro.",
                        onAccionCrear = onCrearRecordatorio,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RecordatorioListUiState.Error -> {
                    ErrorStateView(
                        mensaje = state.mensaje,
                        onReintentar = { viewModel.cargarRecordatorios() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RecordatorioListUiState.ConDatos -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Panel superior de Búsqueda y Filtros
                        FiltrosBarSection(
                            textoBusqueda = state.textoBusqueda,
                            onTextoChange = { viewModel.actualizarBusqueda(it) },
                            filtroPrioridad = state.filtroPrioridad,
                            onPrioridadSelect = { viewModel.seleccionarPrioridad(it) },
                            filtroEstado = state.filtroEstado,
                            onEstadoSelect = { viewModel.seleccionarEstado(it) },
                            onLimpiar = { viewModel.limpiarFiltros() }
                        )

                        if (state.recordatorios.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron resultados para los filtros aplicados.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                items(
                                    items = state.recordatorios,
                                    key = { it.id }
                                ) { recordatorio ->
                                    val config = viewModel.repository.obtenerConfiguracionCategoria(recordatorio.categoria)
                                    RecordatorioItemCard(
                                        recordatorio = recordatorio,
                                        configCategoria = config,
                                        onClick = { onRecordatorioClick(recordatorio.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltrosBarSection(
    textoBusqueda: String,
    onTextoChange: (String) -> Unit,
    filtroPrioridad: Prioridad?,
    onPrioridadSelect: (Prioridad) -> Unit,
    filtroEstado: EstadoRecordatorio?,
    onEstadoSelect: (EstadoRecordatorio) -> Unit,
    onLimpiar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por título o descripción...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtros rápidos:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            if (filtroPrioridad != null || filtroEstado != null || textoBusqueda.isNotEmpty()) {
                TextButton(onClick = onLimpiar, contentPadding = PaddingValues(0.dp)) {
                    Text("Limpiar filtros", fontSize = 11.sp)
                }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Prioridad.entries.toTypedArray()) { prioridad ->
                FilterChip(
                    selected = filtroPrioridad == prioridad,
                    onClick = { onPrioridadSelect(prioridad) },
                    label = { Text("Prioridad ${prioridad.etiqueta}") }
                )
            }
            items(EstadoRecordatorio.todos) { estado ->
                FilterChip(
                    selected = filtroEstado?.nombre == estado.nombre,
                    onClick = { onEstadoSelect(estado) },
                    label = { Text(estado.nombre) }
                )
            }
        }
    }
}
