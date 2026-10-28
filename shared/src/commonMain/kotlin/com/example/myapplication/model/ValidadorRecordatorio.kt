package com.example.myapplication.model

/**
 * Contiene la lógica de validación para asegurar estados válidos en los recordatorios.
 */
object ValidadorRecordatorio {

    /**
     * Evalúa las reglas de negocio de un recordatorio y retorna un [ResultadoValidacion].
     */
    fun validar(recordatorio: Recordatorio): ResultadoValidacion {
        val errores = mutableListOf<String>()

        // Regla 1: El título es obligatorio y debe tener al menos 3 caracteres
        if (recordatorio.titulo.isBlank()) {
            errores.add("El título del recordatorio no puede estar vacío.")
        } else if (recordatorio.titulo.trim().length < 3) {
            errores.add("El título debe contener al menos 3 caracteres.")
        }

        // Regla 2: Si la prioridad es ALTA, se requiere una descripción explicativa
        if (recordatorio.prioridad == Prioridad.ALTA && recordatorio.descripcion.isNullOrBlank()) {
            errores.add("Los recordatorios de prioridad ALTA requieren una descripción.")
        }

        // Regla 3: Si se especifica ubicación/aviso, no puede ser texto en blanco
        if (recordatorio.ubicacionOAviso != null && recordatorio.ubicacionOAviso.isBlank()) {
            errores.add("La ubicación o aviso no puede consistir únicamente en espacios en blanco.")
        }

        // Regla 4: La categoría debe tener un nombre válido
        if (recordatorio.categoria.nombre.isBlank()) {
            errores.add("La categoría asignada no es válida.")
        }

        return if (errores.isEmpty()) {
            ResultadoValidacion.Valido
        } else {
            ResultadoValidacion.Invalido(errores)
        }
    }
}
