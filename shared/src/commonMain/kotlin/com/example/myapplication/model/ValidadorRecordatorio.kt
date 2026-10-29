package com.example.myapplication.model

/**
 * Contiene el resultado de validación específico por cada campo del formulario.
 */
data class EstadoValidacionFormulario(
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null
) {
    /**
     * Retorna verdadero únicamente si no existen errores en ningún campo.
     */
    val esValido: Boolean
        get() = errorTitulo == null &&
                errorDescripcion == null &&
                errorCategoria == null &&
                errorPrioridad == null
}

/**
 * Lógica de validación para los formularios y entidades de recordatorios.
 */
object ValidadorRecordatorio {

    /**
     * Valida los campos individuales de un formulario de creación/edición.
     *
     * Reglas aplicadas según consigna 2:
     * 1. El título debe contener al menos 4 caracteres después de trim().
     * 2. La descripción no debe superar los 200 caracteres.
     * 3. La categoría debe pertenecer a las opciones definidas (no ser nula).
     * 4. La prioridad debe encontrarse seleccionada (no ser nula).
     */
    fun validarFormulario(
        titulo: String,
        descripcion: String,
        categoria: Categoria?,
        prioridad: Prioridad?
    ): EstadoValidacionFormulario {
        val tituloTrimmed = titulo.trim()
        val errorTitulo = when {
            tituloTrimmed.isEmpty() -> "El título es obligatorio."
            tituloTrimmed.length < 4 -> "El título debe contener al menos 4 caracteres."
            else -> null
        }

        val errorDescripcion = when {
            descripcion.length > 200 -> "La descripción no puede superar los 200 caracteres (actual: ${descripcion.length})."
            else -> null
        }

        val errorCategoria = when {
            categoria == null -> "Debe seleccionar una categoría de las opciones definidas."
            else -> null
        }

        val errorPrioridad = when {
            prioridad == null -> "Debe seleccionar un nivel de prioridad."
            else -> null
        }

        return EstadoValidacionFormulario(
            errorTitulo = errorTitulo,
            errorDescripcion = errorDescripcion,
            errorCategoria = errorCategoria,
            errorPrioridad = errorPrioridad
        )
    }

    /**
     * Evalúa la validez completa de una entidad [Recordatorio] existente.
     */
    fun validar(recordatorio: Recordatorio): ResultadoValidacion {
        val validacionForm = validarFormulario(
            titulo = recordatorio.titulo,
            descripcion = recordatorio.descripcion ?: "",
            categoria = recordatorio.categoria,
            prioridad = recordatorio.prioridad
        )

        val errores = mutableListOf<String>()
        validacionForm.errorTitulo?.let { errores.add(it) }
        validacionForm.errorDescripcion?.let { errores.add(it) }
        validacionForm.errorCategoria?.let { errores.add(it) }
        validacionForm.errorPrioridad?.let { errores.add(it) }

        // Regla adicional de negocio de Actividad 1: si la prioridad es ALTA se requiere descripción
        if (recordatorio.prioridad == Prioridad.ALTA && recordatorio.descripcion.isNullOrBlank()) {
            errores.add("Los recordatorios de prioridad ALTA requieren una descripción.")
        }

        return if (errores.isEmpty()) {
            ResultadoValidacion.Valido
        } else {
            ResultadoValidacion.Invalido(errores)
        }
    }
}
