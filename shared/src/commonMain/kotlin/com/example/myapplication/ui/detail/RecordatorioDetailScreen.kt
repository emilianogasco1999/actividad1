package com.example.myapplication.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.model.Prioridad
import com.example.myapplication.ui.components.ErrorStateView
import com.example.myapplication.ui.components.LoadingStateView

/**
 * Pantalla que presenta la información completa del recordatorio recibido mediante su ID (Consignas 1, 3, 5).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioDetailScreen(
    onVolver: () -> Unit,
    viewModel: RecordatorioDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Recordatorio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is RecordatorioDetailUiState.Carga -> {
                    LoadingStateView(
                        mensaje = "Buscando información del recordatorio...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RecordatorioDetailUiState.Error -> {
                    ErrorStateView(
                        mensaje = state.mensaje,
                        onReintentar = { viewModel.cargarDetalle() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RecordatorioDetailUiState.ConDatos -> {
                    val recordatorio = state.recordatorio
                    val config = state.configCategoria
                    val colorCategoria = Color(config.colorHex)

                    val colorPrioridad = when (recordatorio.prioridad) {
                        Prioridad.ALTA -> Color(0xFFD32F2F)
                        Prioridad.MEDIA -> Color(0xFFF57C00)
                        Prioridad.BAJA -> Color(0xFF388E3C)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tarjeta Principal de Información
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "ID de Registro: ${recordatorio.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Text(
                                    text = recordatorio.titulo,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Badge de Categoría
                                    Surface(
                                        color = colorCategoria.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "${recordatorio.categoria.icono} ${recordatorio.categoria.nombre}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            color = colorCategoria,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Badge de Prioridad
                                    Surface(
                                        color = colorPrioridad.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "Prioridad ${recordatorio.prioridad.etiqueta}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            color = colorPrioridad,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                HorizontalDivider(thickness = 0.5.dp)

                                // Descripción Completa
                                Text(
                                    text = "Descripción del Recordatorio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = recordatorio.descripcion ?: "Sin descripción ingresada.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider(thickness = 0.5.dp)

                                // Detalles Adicionales
                                DetailInfoRow(
                                    etiqueta = "Estado actual:",
                                    valor = recordatorio.estado.nombre
                                )

                                DetailInfoRow(
                                    etiqueta = "Ámbito de Categoría:",
                                    valor = config.etiquetaDisplay
                                )

                                if (recordatorio.fechaLimite != null) {
                                    DetailInfoRow(
                                        etiqueta = "Fecha y hora límite:",
                                        valor = recordatorio.fechaLimite
                                    )
                                }

                                if (recordatorio.ubicacionOAviso != null) {
                                    DetailInfoRow(
                                        etiqueta = "Ubicación / Aviso:",
                                        valor = recordatorio.ubicacionOAviso
                                    )
                                }
                            }
                        }

                        // Botón para volver al listado
                        OutlinedButton(
                            onClick = onVolver,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("← Volver a la Lista")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
