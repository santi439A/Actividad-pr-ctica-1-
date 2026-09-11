fun main() {
	var tarea = Tarea(
		titulo = "Entregar informe",
		descripcion = "Preparar y entregar el informe final",
		prioridad = Prioridad.ALTA,
		estado = EstadoTarea.Pendiente
	)
	val incremento = 30

	while (!tarea.estado.esFinal()) {
		println(describir(tarea))
		tarea = avanzar(tarea, incremento)

		check(
			tarea.estado !is EstadoTarea.EnProgreso ||
				tarea.estado.porcentaje <= 100
		) {
			"El porcentaje no puede superar el 100%"
		}
	}

	println(describir(tarea))
}
