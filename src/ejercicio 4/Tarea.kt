data class Tarea(
    val titulo: String,
    val descripcion: String?,
    val prioridad: Prioridad,
    val estado: EstadoTarea
)

fun describir(tarea: Tarea): String {
    return when (tarea.estado){
        EstadoTarea.Pendiente -> "[${tarea.prioridad}] La tarea '${tarea.titulo}' está pendiente."
        is EstadoTarea.EnProgreso -> "[${tarea.prioridad}] La tarea '${tarea.titulo}' está en progreso (${tarea.estado.porcentaje}%)."
        is EstadoTarea.Completada -> "[${tarea.prioridad}] La tarea '${tarea.titulo}' fue completada el ${tarea.estado.fechaFinalizacion}."
        is EstadoTarea.Cancelada -> "[${tarea.prioridad}] La tarea '${tarea.titulo}' fue cancelada. Motivo: ${tarea.estado.motivo}."
    }

    /*
    El error al comentar EstadoTarea.cancelada se encuentra como imagen en esta carpeta el cual indica
    que se debe usar un else ya que el when no es exhaustivo, ya que no se cubren todos los casos 
    posibles de la clase sellada EstadoTarea
    */
}

fun avanzar(tarea: Tarea, incremento: Int): Tarea {
    return when (tarea.estado){
        EstadoTarea.Pendiente -> tarea.copy(estado = EstadoTarea.EnProgreso(incremento))
        is EstadoTarea.EnProgreso -> {
            if (tarea.estado.porcentaje + incremento >= 100) {
                tarea.copy(estado = EstadoTarea.Completada("2026-01-01"))
            } else {
                tarea.copy(estado = EstadoTarea.EnProgreso(tarea.estado.porcentaje + incremento))
            }
        }
        is EstadoTarea.Completada -> tarea
        is EstadoTarea.Cancelada -> tarea
    }
}