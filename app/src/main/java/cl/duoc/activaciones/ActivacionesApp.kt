package cl.duoc.activaciones

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import cl.duoc.activaciones.data.local.AppDatabase
import cl.duoc.activaciones.data.repository.OportunidadRepository
import cl.duoc.activaciones.data.repository.Repositorio
import cl.duoc.activaciones.util.AlertasWorker
import cl.duoc.activaciones.util.Sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Contenedor manual de dependencias (reemplazable por Hilt). */
class ActivacionesApp : Application() {
    lateinit var repo: Repositorio
        private set
    lateinit var oportunidadRepo: OportunidadRepository
        private set
    lateinit var sesion: Sesion
        private set

    override fun onCreate() {
        super.onCreate()
        limpiarBdObsoleta()
        val db = AppDatabase.crear(this)
        repo = Repositorio(db)
        oportunidadRepo = OportunidadRepository(db)
        sesion = Sesion(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch { repo.sembrarSiVacio() }
        AlertasWorker.crearCanal(this)
        AlertasWorker.programar(this)
    }

    /**
     * Borra bases Room de instalaciones muy antiguas que no tienen migración
     * (evita "A migration from X to N was required but not found").
     * Las versiones 3 y 4 se migran sin perder datos (MIGRATION_3_4 y MIGRATION_4_5).
     * Los datos demo se vuelven a sembrar con sembrarSiVacio().
     */
    private fun limpiarBdObsoleta() {
        databaseList()
            .filterNot { it.endsWith("-journal") || it.endsWith("-wal") || it.endsWith("-shm") }
            .forEach { nombre ->
                val version = runCatching {
                    SQLiteDatabase.openDatabase(getDatabasePath(nombre).path, null, SQLiteDatabase.OPEN_READONLY)
                        .use { it.version }
                }.getOrDefault(VERSION_MINIMA_MIGRABLE)
                if (version in 1 until VERSION_MINIMA_MIGRABLE) deleteDatabase(nombre)
            }
    }

    private companion object {
        /** Primera versión de la base que tiene migración en AppDatabase (MIGRATION_3_4). */
        const val VERSION_MINIMA_MIGRABLE = 3
    }
}
