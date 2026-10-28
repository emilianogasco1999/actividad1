package com.example.myapplication.model

/**
 * Prioridad del recordatorio.
 */
enum class Prioridad(val etiqueta: String, val nivel: Int) {
    ALTA("Alta", 3),
    MEDIA("Media", 2),
    BAJA("Baja", 1)
}

/**
 * Estado del recordatorio representado mediante sealed class.
 */
sealed class EstadoRecordatorio(val nombre: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object EnProgreso : EstadoRecordatorio("En Progreso")
    data object Completado : EstadoRecordatorio("Completado")
    data object Archivado : EstadoRecordatorio("Archivado")

    companion object {
        val todos: List<EstadoRecordatorio> get() = listOf(Pendiente, EnProgreso, Completado, Archivado)
    }
}

/**
 * Representa una categoría inmutable.
 */
data class Categoria(
    val id: String,
    val nombre: String,
    val icono: String
)

/**
 * Configuración o metadatos visuales asociados a una categoría.
 */
data class CategoriaConfig(
    val etiquetaDisplay: String,
    val colorHex: Long,
    val descripcion: String
)

/**
 * Representa un recordatorio contextual con atributos obligatorios y opcionales.
 */
data class Recordatorio(
    val id: String,
    val titulo: String,
    val descripcion: String? = null,
    val fechaLimite: String? = null,
    val prioridad: Prioridad,
    val categoria: Categoria,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val ubicacionOAviso: String? = null
)

/**
 * Resultado del proceso de validación de recordatorios.
 */
sealed class ResultadoValidacion {
    data object Valido : ResultadoValidacion()
    data class Invalido(val errores: List<String>) : ResultadoValidacion()
}
