package com.example.myapplication.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.components.CategoriaChipSelector
import com.example.myapplication.ui.components.ControlledTextField
import com.example.myapplication.ui.components.PrioridadChipSelector

/**
 * Pantalla de creación de un nuevo recordatorio (Consignas 1, 2, 3).
 * Administra el formulario reactivo mediante [RecordatorioCreateViewModel] y comunica
 * sus eventos vía lambdas desvinculadas de la navegación interna.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioCreateScreen(
    onGuardarExito: () -> Unit,
    onCancelar: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RecordatorioCreateViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Escuchar el éxito de guardado para disparar la navegación hacia atrás
    LaunchedEffect(uiState.exitoEnvio) {
        if (uiState.exitoEnvio) {
            viewModel.resetExitoEnvio()
            onGuardarExito()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Recordatorio",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de error general si fallara el guardado asincrónico
            if (uiState.errorGeneral != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚠️ ${uiState.errorGeneral}",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp
                    )
                }
            }

            // Campo 1: Título (Obligatorio, min 4 caracteres)
            ControlledTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = "Título del recordatorio *",
                placeholder = "Ej: Reunión de avance de software",
                errorMensaje = uiState.errorTitulo
            )

            // Campo 2: Descripción (Max 200 caracteres)
            ControlledTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = "Descripción",
                placeholder = "Ingrese los detalles de la tarea...",
                singleLine = false,
                maxLines = 4,
                minLines = 2,
                counterText = "${uiState.descripcion.length}/200",
                errorMensaje = uiState.errorDescripcion
            )

            // Campo 3: Selección de Categoría
            CategoriaChipSelector(
                categorias = viewModel.repository.categoriasUnicas,
                categoriaSeleccionada = uiState.categoriaSeleccionada,
                onCategoriaSelect = { viewModel.onCategoriaSelect(it) },
                errorMensaje = uiState.errorCategoria
            )

            // Campo 4: Selección de Prioridad
            PrioridadChipSelector(
                prioridadSeleccionada = uiState.prioridadSeleccionada,
                onPrioridadSelect = { viewModel.onPrioridadSelect(it) },
                errorMensaje = uiState.errorPrioridad
            )

            // Campo 5: Ubicación o Aviso
            ControlledTextField(
                value = uiState.ubicacionOAviso,
                onValueChange = { viewModel.onUbicacionChange(it) },
                label = "Ubicación o Aviso (Opcional)",
                placeholder = "Ej: Sala de reuniones / Zoom"
            )

            // Campo 6: Fecha Límite
            ControlledTextField(
                value = uiState.fechaLimite,
                onValueChange = { viewModel.onFechaLimiteChange(it) },
                label = "Fecha y hora límite (Opcional)",
                placeholder = "Ej: 2025-05-10 15:00"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Confirmación y Cancelación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isEnviando,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }

                // Botón de guardado deshabilitado cuando el formulario es inválido o se está enviando (Consigna 2)
                Button(
                    onClick = { viewModel.guardarRecordatorio() },
                    modifier = Modifier.weight(1.2f),
                    enabled = uiState.isFormValido && !uiState.isEnviando,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isEnviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Text(" Guardando...", modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
