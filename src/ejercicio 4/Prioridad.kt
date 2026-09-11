enum class Prioridad(val nivel: Int) {
    BAJA(1),
    MEDIA(2),
    ALTA(3),
    CRITICA(4);

    fun esUrgente(): Boolean {
        if (nivel >= 3) {
            return true
        }
        return false
    }
}


