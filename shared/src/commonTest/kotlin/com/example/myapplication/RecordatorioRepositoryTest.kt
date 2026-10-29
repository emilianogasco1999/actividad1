package com.example.myapplication

import com.example.myapplication.model.EstadoRecordatorio
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.Recordatorio
import com.example.myapplication.repository.InMemoryRecordatorioRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecordatorioRepositoryTest {

    @Test
    fun testGuardarYObtenerRecordatorioExitoso() = runTest {
        val repository = InMemoryRecordatorioRepository()
        val listaInicial = repository.obtenerRecordatorios().first()
        val totalInicial = listaInicial.size

        val nuevoRecordatorio = Recordatorio(
            id = "REC-TEST-1",
            titulo = "Recordatorio de Prueba Unit",
            descripcion = "Probando el guardado asincrónico",
            prioridad = Prioridad.ALTA,
            categoria = repository.catTrabajo,
            estado = EstadoRecordatorio.Pendiente
        )

        val resultadoGuardado = repository.guardarRecordatorio(nuevoRecordatorio)
        assertTrue(resultadoGuardado.isSuccess)

        val listaActualizada = repository.obtenerRecordatorios().first()
        assertEquals(totalInicial + 1, listaActualizada.size)

        val obtenido = repository.obtenerRecordatorioPorId("REC-TEST-1").first()
        assertNotNull(obtenido)
        assertEquals("Recordatorio de Prueba Unit", obtenido.titulo)
    }

    @Test
    fun testSimulacionErrorGuardado() = runTest {
        val repository = InMemoryRecordatorioRepository()
        repository.simularError(true)

        val nuevoRecordatorio = Recordatorio(
            id = "REC-TEST-2",
            titulo = "Prueba con Error Simulado",
            prioridad = Prioridad.MEDIA,
            categoria = repository.catPersonal
        )

        val resultado = repository.guardarRecordatorio(nuevoRecordatorio)
        assertTrue(resultado.isFailure)
    }
}
