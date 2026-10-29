package com.example.myapplication

import com.example.myapplication.model.Categoria
import com.example.myapplication.model.Prioridad
import com.example.myapplication.model.ValidadorRecordatorio
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidadorRecordatorioTest {

    private val catTest = Categoria("1", "Trabajo", "💼")

    @Test
    fun testTituloConMenosDeCuatroCaracteresInvalido() {
        val valResult = ValidadorRecordatorio.validarFormulario(
            titulo = "   abc  ", // 3 caracteres tras trim()
            descripcion = "Descripción de prueba",
            categoria = catTest,
            prioridad = Prioridad.ALTA
        )

        assertFalse(valResult.esValido)
        assertNotNull(valResult.errorTitulo)
    }

    @Test
    fun testTituloValidoConCuatroCaracteres() {
        val valResult = ValidadorRecordatorio.validarFormulario(
            titulo = "Hola",
            descripcion = "Descripción breve",
            categoria = catTest,
            prioridad = Prioridad.MEDIA
        )

        assertTrue(valResult.esValido)
        assertNull(valResult.errorTitulo)
    }

    @Test
    fun testDescripcionMayorADoscientosCaracteresInvalida() {
        val descripcionLarga = "a".repeat(201)
        val valResult = ValidadorRecordatorio.validarFormulario(
            titulo = "Título Válido",
            descripcion = descripcionLarga,
            categoria = catTest,
            prioridad = Prioridad.BAJA
        )

        assertFalse(valResult.esValido)
        assertNotNull(valResult.errorDescripcion)
    }

    @Test
    fun testFormularioSinCategoriaNiPrioridadInvalido() {
        val valResult = ValidadorRecordatorio.validarFormulario(
            titulo = "Título Correcto",
            descripcion = "Detalles",
            categoria = null,
            prioridad = null
        )

        assertFalse(valResult.esValido)
        assertNotNull(valResult.errorCategoria)
        assertNotNull(valResult.errorPrioridad)
    }
}
