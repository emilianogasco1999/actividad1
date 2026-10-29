package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.CategoriaConfig
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio

/**
 * Componente reutilizable para visualizar una tarjeta de recordatorio en la lista.
 *
 * @param recordatorio Objeto con la información del recordatorio.
 * @param configCategoria Configuración visual de la categoría asociada.
 * @param onClick Callback invocado al tocar la tarjeta para navegar al detalle.
 * @param modifier Parámetro Modifier con valor predeterminado para flexibilidad de diseño.
 */
@Composable
fun RecordatorioItemCard(
    recordatorio: Recordatorio,
    configCategoria: CategoriaConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorCategoria = Color(configCategoria.colorHex)

    val colorPrioridad = when (recordatorio.prioridad) {
        Prioridad.ALTA -> Color(0xFFD32F2F)
        Prioridad.MEDIA -> Color(0xFFF57C00)
        Prioridad.BAJA -> Color(0xFF388E3C)
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Atributos opcionales: Fecha y Ubicación
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 2.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Pie: Estado del recordatorio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = "Tocar para ver detalle ›",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
