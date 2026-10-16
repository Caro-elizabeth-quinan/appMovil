package cl.duoc.activaciones

import android.app.Application
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
        val db = AppDatabase.crear(this)
        repo = Repositorio(db)
        oportunidadRepo = OportunidadRepository(db)
        sesion = Sesion(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch { repo.sembrarSiVacio() }
        AlertasWorker.crearCanal(this)
        AlertasWorker.programar(this)
    }
}
