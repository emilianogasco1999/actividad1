package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Categoria
import com.example.myapplication.model.Prioridad

/**
 * Campo de texto controlado reutilizable con soporte de validación explícita,
 * indicación de error visual y textual (supportingText).
 */
@Composable
fun ControlledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorMensaje: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    counterText: String? = null
) {
    val isError = errorMensaje != null

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(
                imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
            ),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Text("✕", fontSize = 14.sp)
                    }
                }
            },
            supportingText = {
                if (isError) {
                    Text(
                        text = "⚠️ $errorMensaje",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (counterText != null) {
                    Text(text = counterText)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Selector de Categoría mediante Chips interactivos.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriaChipSelector(
    categorias: Set<Categoria>,
    categoriaSeleccionada: Categoria?,
    onCategoriaSelect: (Categoria) -> Unit,
    modifier: Modifier = Modifier,
    errorMensaje: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Categoría *",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (errorMensaje != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { categoria ->
                val selected = categoriaSeleccionada?.id == categoria.id
                FilterChip(
                    selected = selected,
                    onClick = { onCategoriaSelect(categoria) },
                    label = { Text("${categoria.icono} ${categoria.nombre}") },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        if (errorMensaje != null) {
            Text(
                text = "⚠️ $errorMensaje",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

/**
 * Selector de Prioridad mediante Chips interactivos.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrioridadChipSelector(
    prioridadSeleccionada: Prioridad?,
    onPrioridadSelect: (Prioridad) -> Unit,
    modifier: Modifier = Modifier,
    errorMensaje: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Prioridad *",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (errorMensaje != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Prioridad.entries.forEach { prioridad ->
                val selected = prioridadSeleccionada == prioridad
                FilterChip(
                    selected = selected,
                    onClick = { onPrioridadSelect(prioridad) },
                    label = { Text("Prioridad ${prioridad.etiqueta}") },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        if (errorMensaje != null) {
            Text(
                text = "⚠️ $errorMensaje",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
