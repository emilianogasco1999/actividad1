package com.example.myapplication.repository

import com.example.myapplication.model.Categoria
import com.example.myapplication.model.CategoriaConfig
import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Interfaz que abstrae las operaciones del repositorio de recordatorios.
 * Sigue el principio de Inversión de Dependencias (DIP): la capa de presentación dependerá
 * de esta interfaz y no de una implementación concreta o lista estática.
 */
interface RecordatorioRepository {
    val categoriasUnicas: Set<Categoria>
    val mapaConfiguracionCategorias: Map<Categoria, CategoriaConfig>

    fun obtenerRecordatorios(): Flow<List<Recordatorio>>
    fun obtenerRecordatorioPorId(id: String): Flow<Recordatorio?>
    suspend fun guardarRecordatorio(recordatorio: Recordatorio): Result<Unit>
    fun obtenerConfiguracionCategoria(categoria: Categoria): CategoriaConfig
    
    // Métodos para simulación de estados de error/carga en pruebas
    fun simularError(activar: Boolean)
    fun estaErrorSimuladoActivo(): Boolean
}

/**
 * Implementación en memoria del repositorio de recordatorios.
 * Administra el estado de los datos en memoria mediante MutableStateFlow.
 */
class InMemoryRecordatorioRepository : RecordatorioRepository {

    // Categorías predefinidas del sistema
    val catTrabajo = Categoria("1", "Trabajo", "💼")
    val catPersonal = Categoria("2", "Personal", "🏠")
    val catSalud = Categoria("3", "Salud", "🏥")
    val catEducacion = Categoria("4", "Educación", "📚")
    val catFinanzas = Categoria("5", "Finanzas", "💰")

    override val categoriasUnicas: Set<Categoria> = setOf(
        catTrabajo,
        catPersonal,
        catSalud,
        catEducacion,
        catFinanzas
    )

    override val mapaConfiguracionCategorias: Map<Categoria, CategoriaConfig> = mapOf(
        catTrabajo to CategoriaConfig("Ámbito Profesional", 0xFF1976D2, "Proyectos y entregas"),
        catPersonal to CategoriaConfig("Vida Cotidiana", 0xFF388E3C, "Tareas del hogar"),
        catSalud to CategoriaConfig("Bienestar y Salud", 0xFFD32F2F, "Citas médicas y ejercicio"),
        catEducacion to CategoriaConfig("Estudios", 0xFF7B1FA2, "Lecturas y tareas universitarias"),
        catFinanzas to CategoriaConfig("Gestión Económica", 0xFFF57C00, "Pagos y presupuestos")
    )

    private val datosIniciales = listOf(
        Recordatorio(
            id = "REC-001",
            titulo = "Preparar informe del Módulo 2",
            descripcion = "Desarrollar componentes reutilizables y arquitectura ViewModel en Jetpack Compose.",
            fechaLimite = "2025-04-05 18:00",
            prioridad = Prioridad.ALTA,
            categoria = catTrabajo,
            estado = EstadoRecordatorio.EnProgreso,
            ubicacionOAviso = "Campus Virtual"
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
            descripcion = "Leer documentación oficial sobre Navigation y StateFlow.",
            fechaLimite = "2025-04-08 20:00",
            prioridad = Prioridad.BAJA,
            categoria = catEducacion,
            estado = EstadoRecordatorio.Completado
        )
    )

    private val _recordatoriosState = MutableStateFlow<List<Recordatorio>>(datosIniciales)
    private var forzarErrorSimulado: Boolean = false

    override fun simularError(activar: Boolean) {
        forzarErrorSimulado = activar
    }

    override fun estaErrorSimuladoActivo(): Boolean = forzarErrorSimulado

    override fun obtenerRecordatorios(): Flow<List<Recordatorio>> {
        return _recordatoriosState.asStateFlow()
    }

    override fun obtenerRecordatorioPorId(id: String): Flow<Recordatorio?> {
        return _recordatoriosState.map { lista ->
            lista.find { it.id == id }
        }
    }

    override suspend fun guardarRecordatorio(recordatorio: Recordatorio): Result<Unit> {
        // Simular retardo de red o base de datos asincrónica (600ms)
        delay(600L)

        if (forzarErrorSimulado) {
            return Result.failure(Exception("Error de red simulado al intentar guardar el recordatorio."))
        }

        val listaActual = _recordatoriosState.value.toMutableList()
        val index = listaActual.indexOfFirst { it.id == recordatorio.id }

        if (index != -1) {
            listaActual[index] = recordatorio
        } else {
            listaActual.add(0, recordatorio) // Insertar al inicio de la lista
        }

        _recordatoriosState.value = listaActual.toList()
        return Result.success(Unit)
    }

    override fun obtenerConfiguracionCategoria(categoria: Categoria): CategoriaConfig {
        return mapaConfiguracionCategorias[categoria]
            ?: CategoriaConfig("General", 0xFF607D8B, "Sin categoría específica")
    }
}
