package cl.duoc.activaciones.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Perfil { EJECUTIVO, SUPERVISOR }

enum class EstadoCuenta(val etiqueta: String) { ACTIVA("Activa"), INACTIVA("Inactiva") }

enum class EstadoOportunidad(val etiqueta: String) {
    EVENTO_IDENTIFICADO("Evento identificado"),
    CONTACTO_PENDIENTE("Contacto pendiente"),
    CONTACTO_REALIZADO("Contacto realizado"),
    PROPUESTA_ENVIADA("Propuesta enviada"),
    EN_ESPERA_RESPUESTA("En espera de respuesta"),
    PARTICIPACION_CONFIRMADA("Participación confirmada"),
    DESCARTADA("Descartada")
}

enum class Prioridad(val etiqueta: String) { ALTA("Alta"), MEDIA("Media"), BAJA("Baja") }

enum class Potencial(val etiqueta: String) { ALTO("Alto"), MEDIO("Medio"), BAJO("Bajo") }

// Tipos de oportunidad definidos en el caso (sección 3.2)
enum class TipoEvento(val etiqueta: String) {
    EVENTO_CORPORATIVO("Evento corporativo"),
    FERIA_EMPRESARIAL("Feria empresarial"),
    ACTIVIDAD_MUNICIPAL("Actividad municipal"),
    JORNADA_BENEFICIOS("Jornada de beneficios"),
    FERIA_INSTITUCIONAL("Feria institucional"),
    ACTIVIDAD_TRABAJADORES("Actividad de trabajadores"),
    ACTIVACION_COMERCIAL("Activación comercial"),
    OTRO("Otra instancia relevante")
}

// Tipos de acción de seguimiento definidos en el caso (sección 3.2)
enum class TipoAccion(val etiqueta: String) {
    INVESTIGACION_INICIAL("Investigación inicial"),
    BUSQUEDA_CONTACTO("Búsqueda de contacto"),
    PRIMER_CONTACTO("Primer contacto"),
    ENVIO_PRESENTACION("Envío de presentación"),
    ENVIO_PROPUESTA("Envío de propuesta"),
    SOLICITUD_ANTECEDENTES("Solicitud de antecedentes"),
    REUNION("Reunión"),
    LLAMADA("Llamada"),
    SEGUIMIENTO("Seguimiento"),
    RECEPCION_RESPUESTA("Recepción de respuesta"),
    CONFIRMACION("Confirmación"),
    CIERRE("Cierre"),
    CAMBIO_ESTADO("Cambio de estado") // interno: lo genera el sistema
}

// Medio de contacto preferente (sección 3.2, información de contactos)
val MEDIOS_CONTACTO = listOf("Correo", "Teléfono", "WhatsApp", "Presencial")

// Estado del contacto
val ESTADOS_CONTACTO = listOf("Sin contactar", "Contactado", "Interesado", "No interesado")

// Áreas de contacto definidas en el caso
val AREAS_CONTACTO = listOf(
    "Eventos", "Activaciones", "Marketing", "Comunicaciones",
    "Recursos Humanos", "Bienestar", "Beneficios", "Administración"
)

// Tipos de organización definidos en el caso
val TIPOS_ORGANIZACION = listOf(
    "Empresa", "Municipalidad", "Institución pública", "Institución privada",
    "Organizador de eventos", "Centro comercial", "Asociación", "Otra entidad"
)

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: Perfil,
    val zona: String,
    val area: String = "Comercial",
    val direccion: String = "",
    val estadoCuenta: EstadoCuenta = EstadoCuenta.ACTIVA,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis()
)

@Entity(tableName = "organizacion")
data class Organizacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: String,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String = "",
    val observaciones: String = ""
)

@Entity(tableName = "contacto", indices = [Index("organizacionId")])
data class Contacto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizacionId: Long,
    val nombre: String,
    val cargo: String,
    val area: String,
    val correo: String,
    val telefono: String,
    val medioPreferente: String = "Correo",
    val estado: String = "Sin contactar",
    val fechaUltimoContacto: Long? = null,
    val observaciones: String = ""
)

@Entity(tableName = "seguimiento", indices = [Index("oportunidadId")])
data class Seguimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: Long,
    val tipoAccion: TipoAccion,
    val fechaHora: Long,
    val resultado: String,
    val proximaAccion: String = "",
    val fechaProximoSeguimiento: Long? = null
)

@Entity(tableName = "resultado_activacion")
data class ResultadoActivacion(
    @PrimaryKey val oportunidadId: Long,
    val asistentes: Int,
    val contactosObtenidos: Int,
    val inscripciones: Int,
    val evaluacion: String
)