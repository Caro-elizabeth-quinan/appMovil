package cl.duoc.activaciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.activaciones.model.Contacto
import cl.duoc.activaciones.model.Organizacion
import cl.duoc.activaciones.model.ResultadoActivacion
import cl.duoc.activaciones.model.Seguimiento
import cl.duoc.activaciones.model.Usuario

/** v3 -> v4: se agrega la dirección del usuario sin borrar ningún dato. */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE usuario ADD COLUMN direccion TEXT NOT NULL DEFAULT ''")
    }
}

/**
 * v4 -> v5: campos del caso para organización y contacto, y tipos de evento/acción
 * alineados al caso. Los valores antiguos se reasignan para no perder datos.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE organizacion ADD COLUMN sitioWeb TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE organizacion ADD COLUMN observaciones TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE contacto ADD COLUMN medioPreferente TEXT NOT NULL DEFAULT 'Correo'")
        db.execSQL("ALTER TABLE contacto ADD COLUMN estado TEXT NOT NULL DEFAULT 'Sin contactar'")
        db.execSQL("ALTER TABLE contacto ADD COLUMN fechaUltimoContacto INTEGER")
        db.execSQL("ALTER TABLE contacto ADD COLUMN observaciones TEXT NOT NULL DEFAULT ''")

        val tipos = mapOf(
            "FERIA" to "FERIA_EMPRESARIAL",
            "FERIA_LABORAL" to "FERIA_INSTITUCIONAL",
            "SEMINARIO" to "EVENTO_CORPORATIVO",
            "CHARLA" to "ACTIVIDAD_TRABAJADORES",
            "ACTIVACION" to "ACTIVACION_COMERCIAL"
        )
        tipos.forEach { (viejo, nuevo) ->
            db.execSQL("UPDATE oportunidad SET tipo = '$nuevo' WHERE tipo = '$viejo'")
        }
        val acciones = mapOf("CORREO" to "SEGUIMIENTO", "VISITA" to "REUNION", "NOTA" to "SEGUIMIENTO")
        acciones.forEach { (viejo, nuevo) ->
            db.execSQL("UPDATE seguimiento SET tipoAccion = '$nuevo' WHERE tipoAccion = '$viejo'")
        }
    }
}

@Database(
    entities = [Usuario::class, Organizacion::class, Contacto::class, OportunidadEntity::class,
        Seguimiento::class, ResultadoActivacion::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun organizacionDao(): OrganizacionDao
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun seguimientoDao(): SeguimientoDao
    abstract fun resultadoDao(): ResultadoDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "activaciones.db")
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                .build()
    }
}