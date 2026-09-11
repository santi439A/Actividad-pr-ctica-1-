package ejercicio6

import kotlinx.coroutines.*

/*
 * Corutinas (ejercicio 6)
 *
 * Simula la carga de datos de una aplicación Android: se comparan los tiempos
 * de una ejecución secuencial con los de una ejecución en paralelo, ambas
 * sobre las mismas consultas simuladas con delay().
 */

/** Consulta al servidor con 1500 ms de latencia: devuelve el perfil del usuario. */
suspend fun obtenerPerfil(): String {
    delay(1500)
    return "Perfil cargado: Daniel García"
}

/** Consulta al servidor con 1000 ms de latencia: devuelve el número de notificaciones. */
suspend fun obtenerNotificaciones(): Int {
    delay(1000)
    return 5
}

/** Consulta al servidor con 2000 ms de latencia: devuelve el número de mensajes. */
suspend fun obtenerMensajes(): Int {
    delay(2000)
    return 12
}

fun main() {
    runBlocking {
        // Corutina auxiliar que informa el progreso: imprime "Cargando..." cada 300 ms
        // mientras las consultas se completan y se cancela con el Job que devuelve launch.
        val jobCargando = launch {
            while (isActive) {
                println("Cargando...")
                delay(300)
            }
        }

        // Ejecución secuencial: cada consulta espera a que termine la anterior.
        val inicioSecuencial = System.currentTimeMillis()

        val perfilSecuencial = obtenerPerfil()
        val notificacionesSecuenciales = obtenerNotificaciones()
        val mensajesSecuenciales = obtenerMensajes()

        val tiempoSecuencial = System.currentTimeMillis() - inicioSecuencial
        println(
            "Secuencial -> Perfil: $perfilSecuencial | " +
                "Notificaciones: $notificacionesSecuenciales | Mensajes: $mensajesSecuenciales"
        )
        println("Tiempo secuencial: $tiempoSecuencial ms")

        // Ejecución en paralelo: las tres consultas se lanzan a la vez con async
        // y se esperan los resultados con await().
        val inicioParalelo = System.currentTimeMillis()

        val perfilDeferred = async { obtenerPerfil() }
        val notificacionesDeferred = async { obtenerNotificaciones() }
        val mensajesDeferred = async { obtenerMensajes() }

        val perfilParalelo = perfilDeferred.await()
        val notificacionesParalelas = notificacionesDeferred.await()
        val mensajesParalelos = mensajesDeferred.await()

        val tiempoParalelo = System.currentTimeMillis() - inicioParalelo
        println(
            "Paralelo -> Perfil: $perfilParalelo | " +
                "Notificaciones: $notificacionesParalelas | Mensajes: $mensajesParalelos"
        )
        println("Tiempo paralelo: $tiempoParalelo ms")

        // Comparación de tiempos.
        println("Diferencia (secuencial - paralelo): ${tiempoSecuencial - tiempoParalelo} ms")

        /*
        Explicación de la diferencia de tiempos:

        En el caso secuencial cada llamada bloquea el flujo hasta terminar, de modo que el
        total es la suma de los tres retardos (≈ 1500 + 1000 + 2000 = 4500 ms).

        Con async() las tres consultas se lanzan a la vez dentro del mismo ámbito de runBlocking;
        delay() suspende cada corutina sin bloquear a las demás, por lo que los tres retardos
        transcurren de forma concurrente. await() solo espera cada resultado, así que el tiempo
        total se aproxima a la consulta más lenta (2000 ms) más un pequeño overhead, y no a la
        suma de los tres.
        */

        // Fin de la carga: se cancela la corutina de progreso y se espera a que termine
        // el ciclo, quedando el scope sin corutinas activas.
        jobCargando.cancelAndJoin()
        println("Carga finalizada.")
    }
}