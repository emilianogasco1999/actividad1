package com.example.myapplication.repository

import com.example.myapplication.model.Categoria
import com.example.myapplication.model.CategoriaConfig
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio

/**
 * Repositorio encargado de gestionar los datos simulados demostrando el uso de Lists, Sets y Maps.
 */
class RecordatorioRepository {

    // 1. Definición de Categorías
    val catTrabajo = Categoria("1", "Trabajo", "💼")
    val catPersonal = Categoria("2", "Personal", "🏠")
    val catSalud = Categoria("3", "Salud", "🏥")
    val catEducacion = Categoria("4", "Educación", "📚")
    val catFinanzas = Categoria("5", "Finanzas", "💰")

    /**
     * CONSIGNA 3 - USO DE SETS:
     * Se utiliza un Set para almacenar las categorías únicas del sistema.
     * Si intentamos añadir duplicados, el Set los ignora automáticamente.
     */
    val categoriasUnicas: Set<Categoria> = setOf(
        catTrabajo,
        catPersonal,
        catSalud,
        catEducacion,
        catFinanzas,
        catTrabajo // Intencionalmente repetida en la declaración para verificar que el Set elimina duplicados
    )

    /**
     * CONSIGNA 3 - USO DE MAPS:
     * Relaciona cada objeto Categoria con su configuración visual y etiqueta descriptiva.
     */
    val mapaConfiguracionCategorias: Map<Categoria, CategoriaConfig> = mapOf(
        catTrabajo to CategoriaConfig("Ámbito Profesional", 0xFF1976D2, "Proyectos, reuniones y entregas de software"),
        catPersonal to CategoriaConfig("Vida Cotidiana", 0xFF388E3C, "Tareas del hogar y compromisos familiares"),
        catSalud to CategoriaConfig("Bienestar y Salud", 0xFFD32F2F, "Citas médicas, medicamentos y ejercicios"),
        catEducacion to CategoriaConfig("Estudios y Cap.", 0xFF7B1FA2, "Lecturas, cursos y tareas universitarias"),
        catFinanzas to CategoriaConfig("Gestión Económica", 0xFFF57C00, "Pagos de servicios y presupuestos")
    )

    /**
     * CONSIGNA 3 - USO DE LISTS:
     * Almacena el conjunto inicial de recordatorios simulados.
     */
    val listaRecordatoriosSimulados: List<Recordatorio> = listOf(
        Recordatorio(
            id = "REC-001",
            titulo = "Preparar informe del Módulo 1",
            descripcion = "Modelar clases de Kotlin y verificar ejecución de la pantalla Compose.",
            fechaLimite = "2025-04-05 18:00",
            prioridad = Prioridad.ALTA,
            categoria = catTrabajo,
            estado = EstadoRecordatorio.EnProgreso,
            ubicacionOAviso = "Oficina / Campus Virtual"
        ),
        Recordatorio(
            id = "REC-002",
            titulo = "Comprar insumos médicos",
            descripcion = "Retirar receta de la farmacia central.",
            fechaLimite = "2025-04-02 10:00",
            prioridad = Prioridad.MEDIA,
            categoria = catSalud,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOAviso = "Farmacia San José"
        ),
        Recordatorio(
            id = "REC-003",
            titulo = "Pagar servicio de internet",
            descripcion = "Abonar la factura mensual antes del vencimiento.",
            fechaLimite = "2025-04-10 23:59",
            prioridad = Prioridad.ALTA,
            categoria = catFinanzas,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOAviso = "Banca en línea"
        ),
        Recordatorio(
            id = "REC-004",
            titulo = "Revisar bibliografía de Jetpack Compose",
            descripcion = "Leer documentación oficial sobre Material 3 y State management.",
            fechaLimite = "2025-04-08 20:00",
            prioridad = Prioridad.BAJA,
            categoria = catEducacion,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = "REC-005",
            titulo = "Organizar mantenimiento de vehículo",
            descripcion = "Llevar el coche a revisión técnica anual.",
            fechaLimite = null,
            prioridad = Prioridad.MEDIA,
            categoria = catPersonal,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOAviso = "Taller Mecánico Central"
        ),
        Recordatorio(
            id = "REC-006",
            titulo = "Prueba de caso inválido para validación",
            descripcion = null, // Inválido porque prioridad ALTA requiere descripción
            fechaLimite = "2025-04-15",
            prioridad = Prioridad.ALTA,
            categoria = catTrabajo,
            estado = EstadoRecordatorio.Pendiente
        )
    )

    /**
     * Filtra la lista de recordatorios según los criterios seleccionados.
     */
    fun filtrarRecordatorios(
        prioridadFiltro: Prioridad? = null,
        estadoFiltro: EstadoRecordatorio? = null,
        categoriaFiltro: Categoria? = null,
        textoBusqueda: String = ""
    ): List<Recordatorio> {
        return listaRecordatoriosSimulados.filter { recordatorio ->
            val coincidePrioridad = prioridadFiltro == null || recordatorio.prioridad == prioridadFiltro
            val coincideEstado = estadoFiltro == null || recordatorio.estado.nombre == estadoFiltro.nombre
            val coincideCategoria = categoriaFiltro == null || recordatorio.categoria.id == categoriaFiltro.id
            val coincideTexto = textoBusqueda.isBlank() ||
                    recordatorio.titulo.contains(textoBusqueda, ignoreCase = true) ||
                    (recordatorio.descripcion?.contains(textoBusqueda, ignoreCase = true) == true)

            coincidePrioridad && coincideEstado && coincideCategoria && coincideTexto
        }
    }

    /**
     * Obtiene la configuración de una categoría utilizando el Map.
     */
    fun obtenerConfiguracionCategoria(categoria: Categoria): CategoriaConfig {
        return mapaConfiguracionCategorias[categoria]
            ?: CategoriaConfig("General", 0xFF607D8B, "Sin categoría específica")
    }
}
