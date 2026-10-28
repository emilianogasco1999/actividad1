package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Categoria
import com.example.myapplication.model.CategoriaConfig
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio
import com.example.myapplication.model.ResultadoValidacion
import com.example.myapplication.model.ValidadorRecordatorio
import com.example.myapplication.repository.RecordatorioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatoriosScreen() {
    val repository = remember { RecordatorioRepository() }

    var textoBusqueda by remember { mutableStateOf("") }
    var prioridadSeleccionada by remember { mutableStateOf<Prioridad?>(null) }
    var estadoSeleccionado by remember { mutableStateOf<EstadoRecordatorio?>(null) }

    val recordatoriosFiltrados = remember(textoBusqueda, prioridadSeleccionada, estadoSeleccionado) {
        repository.filtrarRecordatorios(
            prioridadFiltro = prioridadSeleccionada,
            estadoFiltro = estadoSeleccionado,
            textoBusqueda = textoBusqueda
        )
    }

    Scaffold(
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
                            text = "Programación Mobile - Actividad 1",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Sección 1: Panel de Colecciones (Consigna 3)
            item {
                ResumenColeccionesCard(
                    totalSetCategorias = repository.categoriasUnicas.size,
                    totalListRecordatorios = repository.listaRecordatoriosSimulados.size,
                    categoriasConfigMap = repository.mapaConfiguracionCategorias
                )
            }

            // Sección 2: Filtros y Búsqueda
            item {
                FiltrosYBusquedaSection(
                    textoBusqueda = textoBusqueda,
                    onTextoBusquedaChange = { textoBusqueda = it },
                    prioridadSeleccionada = prioridadSeleccionada,
                    onPrioridadSelect = {
                        prioridadSeleccionada = if (prioridadSeleccionada == it) null else it
                    },
                    estadoSeleccionado = estadoSeleccionado,
                    onEstadoSelect = {
                        estadoSeleccionado = if (estadoSeleccionado?.nombre == it.nombre) null else it
                    }
                )
            }

            // Sección 3: Encabezado de Lista
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lista de Recordatorios (${recordatoriosFiltrados.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (prioridadSeleccionada != null || estadoSeleccionado != null || textoBusqueda.isNotEmpty()) {
                        TextButton(onClick = {
                            textoBusqueda = ""
                            prioridadSeleccionada = null
                            estadoSeleccionado = null
                        }) {
                            Text("Limpiar filtros", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sección 4: Items de Recordatorios
            if (recordatoriosFiltrados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron recordatorios con los filtros aplicados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(recordatoriosFiltrados, key = { it.id }) { recordatorio ->
                    val configCategoria = repository.obtenerConfiguracionCategoria(recordatorio.categoria)
                    RecordatorioItemCard(
                        recordatorio = recordatorio,
                        configCategoria = configCategoria
                    )
                }
            }
        }
    }
}

/**
 * Muestra un resumen del uso de Set, List y Map (Consigna 3).
 */
@Composable
fun ResumenColeccionesCard(
    totalSetCategorias: Int,
    totalListRecordatorios: Int,
    categoriasConfigMap: Map<Categoria, CategoriaConfig>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📊 Estructura de Datos (Colecciones Kotlin)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoBadge(
                    titulo = "Set (Únicas)",
                    valor = "$totalSetCategorias Categorías",
                    color = Color(0xFF1976D2)
                )
                InfoBadge(
                    titulo = "List (Elementos)",
                    valor = "$totalListRecordatorios Recordatorios",
                    color = Color(0xFF388E3C)
                )
                InfoBadge(
                    titulo = "Map (Metadatos)",
                    valor = "${categoriasConfigMap.size} Relaciones",
                    color = Color(0xFF7B1FA2)
                )
            }
        }
    }
}

@Composable
fun InfoBadge(titulo: String, valor: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
        Text(text = valor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Sección de controles de búsqueda y filtros interactivos.
 */
@Composable
fun FiltrosYBusquedaSection(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    prioridadSeleccionada: Prioridad?,
    onPrioridadSelect: (Prioridad) -> Unit,
    estadoSeleccionado: EstadoRecordatorio?,
    onEstadoSelect: (EstadoRecordatorio) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por título o descripción...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Text("Filtrar por Prioridad:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Prioridad.entries.toTypedArray()) { prioridad ->
                FilterChip(
                    selected = prioridadSeleccionada == prioridad,
                    onClick = { onPrioridadSelect(prioridad) },
                    label = { Text("Prioridad ${prioridad.etiqueta}") }
                )
            }
        }

        Text("Filtrar por Estado:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(EstadoRecordatorio.todos) { estado ->
                FilterChip(
                    selected = estadoSeleccionado?.nombre == estado.nombre,
                    onClick = { onEstadoSelect(estado) },
                    label = { Text(estado.nombre) }
                )
            }
        }
    }
}

/**
 * Tarjeta individual de Recordatorio con Material 3 (Consignas 2 y 4).
 */
@Composable
fun RecordatorioItemCard(
    recordatorio: Recordatorio,
    configCategoria: CategoriaConfig
) {
    val resultadoValidacion = remember(recordatorio) {
        ValidadorRecordatorio.validar(recordatorio)
    }

    val colorCategoria = Color(configCategoria.colorHex)

    val colorPrioridad = when (recordatorio.prioridad) {
        Prioridad.ALTA -> Color(0xFFD32F2F)
        Prioridad.MEDIA -> Color(0xFFF57C00)
        Prioridad.BAJA -> Color(0xFF388E3C)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado: Título + Categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recordatorio.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recordatorio.categoria.icono} ${recordatorio.categoria.nombre} • ${configCategoria.etiquetaDisplay}",
                        fontSize = 11.sp,
                        color = colorCategoria,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Insignia de Prioridad
                Surface(
                    color = colorPrioridad.copy(alpha = 0.15f),
                    contentColor = colorPrioridad,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = recordatorio.prioridad.etiqueta,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Descripción (opcional)
            if (!recordatorio.descripcion.isNullOrEmpty()) {
                Text(
                    text = recordatorio.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Atributos Opcionales: Fecha límite y Ubicación/Aviso
            if (recordatorio.fechaLimite != null || recordatorio.ubicacionOAviso != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    recordatorio.fechaLimite?.let { fecha ->
                        Text(
                            text = "📅 $fecha",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    recordatorio.ubicacionOAviso?.let { ubicacion ->
                        Text(
                            text = "📍 $ubicacion",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Pie: Estado + Estado de Validación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado del recordatorio (Sealed Class)
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Estado: ${recordatorio.estado.nombre}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Indicador de Validación de la regla de negocio
                when (resultadoValidacion) {
                    is ResultadoValidacion.Valido -> {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "✓ Válido",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    is ResultadoValidacion.Invalido -> {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFC62828),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚠️ Regla violada",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Si es inválido, mostrar el detalle del error de validación
            if (resultadoValidacion is ResultadoValidacion.Invalido) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Errores de validación detectados:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    resultadoValidacion.errores.forEach { error ->
                        Text(
                            text = "• $error",
                            fontSize = 10.sp,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }
            }
        }
    }
}
