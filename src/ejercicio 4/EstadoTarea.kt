sealed class EstadoTarea {
    object Pendiente : EstadoTarea()
    class EnProgreso(val porcentaje: Int) : EstadoTarea()
    class Completada(val fechaFinalizacion: String) : EstadoTarea()
    class Cancelada(val motivo: String) : EstadoTarea()
}

fun EstadoTarea.esFinal(): Boolean {
    return when (this) {
        EstadoTarea.Pendiente -> false
        is EstadoTarea.EnProgreso -> false
        is EstadoTarea.Completada -> true
        is EstadoTarea.Cancelada -> true
    }
}