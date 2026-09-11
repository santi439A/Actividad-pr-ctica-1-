package ejercicio5

import java.util.Calendar

/*
 * Sistema hospitalario (ejercicio 5)
 *
 * Modelo de dominio hospitalario construido con herencia e interfaces:
 * una jerarquía de personas (médicos y pacientes), datos demográficos compartidos
 * y un hospital que gestiona el registro, el contacto y la liquidación de salarios.
 */

/**
 * Entidad identificable dentro del sistema.
 *
 * La implementación por defecto de [resumenIdentidad] combina el nombre y la
 * identificación; por ello la interfaz declara ambas propiedades.
 */
interface Identificable {
    val nombre: String
    val identificacion: String

    fun resumenIdentidad(): String = "Nombre: $nombre, Identificación: $identificacion"
}

/** Género con el que se registra a una persona. */
enum class Genero {
    MASCULINO, FEMENINO, OTRO
}

/** Especialidades médicas que ofrece el hospital. */
enum class Especialidad {
    PEDIATRIA, CARDIOLOGIA, NEUROLOGIA
}

/** Ubicación residencial de un paciente. */
data class Direccion(
    val calle: String,
    val ciudad: String,
    val codigoPostal: String
)

/**
 * Clase base del sistema hospitalario.
 *
 * El bloque [init] valida que el nombre no esté vacío; el correo es opcional
 * y se expone como propiedad nullable para poder evaluar el operador Elvis en [Hospital.contactar].
 */
open class Persona(
    override val nombre: String,
    override val identificacion: String,
    val genero: Genero,
    val correo: String? = null
) : Identificable {

    init {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre no puede estar vacío")
        }
    }

    override fun toString(): String =
        "Persona(nombre='$nombre', identificacion='$identificacion', genero=$genero)"
}

/**
 * Profesional de la salud con especialidad, salario y año de ingreso.
 *
 * El constructor secundario delega en el primario asignando un salario base y
 * el año en curso como fecha de ingreso, de modo que la creación solo requiera
 * nombre, identificación, género y especialidad.
 */
class Medico(
    nombre: String,
    identificacion: String,
    genero: Genero,
    val especialidad: Especialidad,
    val salario: Double,
    val anioIngreso: Int,
    correo: String? = null
) : Persona(nombre, identificacion, genero, correo) {

    constructor(
        nombre: String,
        identificacion: String,
        genero: Genero,
        especialidad: Especialidad
    ) : this(
        nombre,
        identificacion,
        genero,
        especialidad,
        salario = SALARIO_BASE,
        anioIngreso = Calendar.getInstance().get(Calendar.YEAR)
    )

    /** Años transcurridos desde el ingreso respecto a [anioActual]. */
    fun antiguedad(anioActual: Int = 2026): Int = anioActual - anioIngreso

    /** Amplía el resumen heredado con la especialidad del médico. */
    override fun resumenIdentidad(): String =
        "${super.resumenIdentidad()} | Especialidad: $especialidad"

    companion object {
        /** Salario asignado por el constructor secundario cuando no se especifica uno. */
        val SALARIO_BASE = 3_000_000.0
    }
}

/** Persona que recibe atención médica, con teléfono de contacto y dirección. */
class Paciente(
    nombre: String,
    identificacion: String,
    genero: Genero,
    val telefono: String,
    val direccion: Direccion,
    correo: String? = null
) : Persona(nombre, identificacion, genero, correo)

/** Registro de médicos y pacientes con las operaciones de gestión del hospital. */
class Hospital {

    private val medicos = mutableListOf<Medico>()
    private val pacientes = mutableListOf<Paciente>()

    fun agregarMedico(medico: Medico) {
        medicos.add(medico)
    }

    /** Elimina un médico del registro; devuelve true si estaba registrado. */
    fun eliminarMedico(medico: Medico): Boolean = medicos.remove(medico)

    fun agregarPaciente(paciente: Paciente) {
        pacientes.add(paciente)
    }

    /** Elimina un paciente del registro; devuelve true si estaba registrado. */
    fun eliminarPaciente(paciente: Paciente): Boolean = pacientes.remove(paciente)

    /** Cantidad de médicos actualmente registrados. */
    fun cantidadMedicos(): Int = medicos.size

    /** Cantidad de pacientes actualmente registrados. */
    fun cantidadPacientes(): Int = pacientes.size

    /**
     * Suma de los salarios de los médicos de una especialidad concreta,
     * recorriendo la lista con un ciclo.
     */
    fun totalSalarios(especialidad: Especialidad): Double {
        var total = 0.0
        for (medico in medicos) {
            if (medico.especialidad == especialidad) {
                total += medico.salario
            }
        }
        return total
    }

    /**
     * Médico con mayor antigüedad de servicio.
     *
     * @return null cuando no hay médicos registrados; el acumulador es un tipo
     *         nullable que se retorna sin forzar con !!.
     */
    fun medicoConMasAntiguedad(): Medico? {
        var mejor: Medico? = null
        var mayorAntiguedad = -1
        for (medico in medicos) {
            val antiguedad = medico.antiguedad()
            if (antiguedad > mayorAntiguedad) {
                mayorAntiguedad = antiguedad
                mejor = medico
            }
        }
        return mejor
    }

    /**
     * Correo de la persona (médico o paciente) asociada a una identificación.
     *
     * El encadenamiento con el operador Elvis cubre los dos casos nulos:
     * persona inexistente y correo no registrado.
     */
    fun contactar(identificacion: String): String =
        (medicos + pacientes).firstOrNull { it.identificacion == identificacion }
            ?.correo ?: "Sin correo registrado"
}

fun main() {
    val hospital = Hospital()

    // Médicos: dos con el constructor primario (salario, ingreso y correo explícitos)
    // y uno con el secundario (salario base y año en curso).
    val draPerez = Medico(
        "Lucía Pérez", "M-1001", Genero.FEMENINO, Especialidad.CARDIOLOGIA,
        salario = 8_500_000.0, anioIngreso = 2015, correo = "lucia.perez@hospital.com"
    )
    val drGomez = Medico(
        "Carlos Gómez", "M-1002", Genero.MASCULINO, Especialidad.PEDIATRIA
    )
    val draRios = Medico(
        "Ana Ríos", "M-1003", Genero.FEMENINO, Especialidad.NEUROLOGIA,
        salario = 7_000_000.0, anioIngreso = 2018
    )

    // Pacientes: uno con correo y dos sin correo.
    val paciente1 = Paciente(
        "Juan Rodríguez", "P-2001", Genero.MASCULINO,
        "3005550101", Direccion("Calle 10 #20-30", "Medellín", "050001"),
        correo = "juan.rodriguez@gmail.com"
    )
    val paciente2 = Paciente(
        "María López", "P-2002", Genero.FEMENINO,
        "3005550202", Direccion("Carrera 45 #60-12", "Bogotá", "110111")
    )
    val paciente3 = Paciente(
        "Carlos Herrera", "P-2003", Genero.MASCULINO,
        "3005550303", Direccion("Avenida 80 #45-5", "Cali", "760045")
    )

    hospital.agregarMedico(draPerez)
    hospital.agregarMedico(drGomez)
    hospital.agregarMedico(draRios)
    hospital.agregarPaciente(paciente1)
    hospital.agregarPaciente(paciente2)
    hospital.agregarPaciente(paciente3)

    println("Médicos registrados: ${hospital.cantidadMedicos()}")
    println("Pacientes registrados: ${hospital.cantidadPacientes()}")

    // resumenIdentidad(): implementación por defecto de la interfaz, ampliada en Medico.
    listOf(draPerez, drGomez, draRios).forEach {
        println("- ${it.resumenIdentidad()} | Antigüedad: ${it.antiguedad()}")
    }
    listOf(paciente1, paciente2, paciente3).forEach {
        println("- ${it.resumenIdentidad()}")
    }

    // Total de salarios por especialidad.
    println("Salario total CARDIOLOGIA: ${hospital.totalSalarios(Especialidad.CARDIOLOGIA)}")
    println("Salario total PEDIATRIA: ${hospital.totalSalarios(Especialidad.PEDIATRIA)}")

    // Médico con más antigüedad (Lucía Pérez, ingreso en 2015).
    val masAntiguo = hospital.medicoConMasAntiguedad()
    println("Médico con más antigüedad: ${masAntiguo?.resumenIdentidad() ?: "Ninguno"}")

    // Baja de un médico y verificación de que se refleja en el registro.
    println("Eliminando a Carlos Gómez: ${hospital.eliminarMedico(drGomez)}")
    println("Médicos después de eliminar: ${hospital.cantidadMedicos()}")

    // contactar(): con correo, sin correo, identificación eliminada e inexistente.
    println("Contactar M-1001: ${hospital.contactar("M-1001")}")
    println("Contactar M-1002 (eliminado): ${hospital.contactar("M-1002")}")
    println("Contactar M-1003 (sin correo): ${hospital.contactar("M-1003")}")
    println("Contactar P-2001 (con correo): ${hospital.contactar("P-2001")}")
    println("Contactar P-2002 (sin correo): ${hospital.contactar("P-2002")}")
    println("Contactar X-9999 (no existe): ${hospital.contactar("X-9999")}")

    // Verificación del comportamiento ante un hospital sin médicos registrados.
    val hospitalVacio = Hospital()
    println("Hospital vacío -> medicoConMasAntiguedad: ${hospitalVacio.medicoConMasAntiguedad()}")

    // Verificación de la validación del nombre vacío en el bloque init de Persona.
    val resultadoValidacion = try {
        Medico("", "M-0000", Genero.OTRO, Especialidad.PEDIATRIA)
        "No lanzó excepción"
    } catch (e: IllegalArgumentException) {
        e.message
    }
    println("Validación de nombre vacío: $resultadoValidacion")
}