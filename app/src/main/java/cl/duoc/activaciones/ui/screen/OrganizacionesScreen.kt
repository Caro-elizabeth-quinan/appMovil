package cl.duoc.activaciones.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.activaciones.model.Contacto
import cl.duoc.activaciones.model.Organizacion
import cl.duoc.activaciones.model.AREAS_CONTACTO
import cl.duoc.activaciones.model.ESTADOS_CONTACTO
import cl.duoc.activaciones.model.MEDIOS_CONTACTO
import cl.duoc.activaciones.model.REGIONES
import cl.duoc.activaciones.model.TIPOS_ORGANIZACION
import cl.duoc.activaciones.model.correoValido
import cl.duoc.activaciones.util.aFecha
import cl.duoc.activaciones.model.comunasDe
import cl.duoc.activaciones.ui.components.Campo
import cl.duoc.activaciones.ui.components.CampoSelector
import cl.duoc.activaciones.ui.components.ConfirmarEliminacion
import cl.duoc.activaciones.ui.components.OportunidadCard
import cl.duoc.activaciones.ui.components.app
import cl.duoc.activaciones.ui.components.vmFactory
import cl.duoc.activaciones.ui.theme.barraColores
import cl.duoc.activaciones.viewmodel.DetalleViewModel
import cl.duoc.activaciones.viewmodel.OrganizacionDetalleViewModel
import cl.duoc.activaciones.viewmodel.OrganizacionesViewModel

@Composable
fun OrganizacionesScreen(alVolver: () -> Unit, alAbrir: (Long) -> Unit) {
    val app = app()
    val vm: OrganizacionesViewModel = viewModel(factory = vmFactory { OrganizacionesViewModel(app.repo) })
    val lista by vm.organizaciones.collectAsState()
    var dialogo by remember { mutableStateOf(false) }
    var porEliminar by remember { mutableStateOf<Organizacion?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = barraColores(),
                title = { Text("Organizaciones") },
                navigationIcon = { IconButton(onClick = alVolver) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogo = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva organización") }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            items(lista, key = { it.id }) { o ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp).clickable { alAbrir(o.id) }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(o.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                            Text("${o.tipo} · ${o.rubro}")
                            Text("${o.comuna}, ${o.region}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { porEliminar = o }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar organización", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { o ->
        ConfirmarEliminacion(
            titulo = "Eliminar organización",
            mensaje = "Se eliminará \"${o.nombre}\" y sus contactos. Sus oportunidades se conservan sin organización asignada.",
            alCancelar = { porEliminar = null },
            alConfirmar = {
                vm.eliminar(o)
                porEliminar = null
            }
        )
    }
    if (dialogo) {
        var nombre by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf("") }
        var rubro by remember { mutableStateOf("") }
        var comuna by remember { mutableStateOf("") }
        var region by remember { mutableStateOf("") }
        var sitioWeb by remember { mutableStateOf("") }
        var observaciones by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { dialogo = false },
            title = { Text("Nueva organización") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Campo("Nombre", nombre) { nombre = it }
                    CampoSelector("Tipo de organización", TIPOS_ORGANIZACION, tipo.ifBlank { null }, { it }) { tipo = it }
                    Campo("Rubro", rubro) { rubro = it }
                    CampoSelector("Región", REGIONES, region.ifBlank { null }, { it }) {
                        region = it
                        comuna = ""
                    }
                    CampoSelector(
                        "Comuna", comunasDe(region), comuna.ifBlank { null }, { it },
                        habilitado = region.isNotBlank()
                    ) { comuna = it }
                    Campo("Sitio web (ficticio o público)", sitioWeb) { sitioWeb = it }
                    Campo("Observaciones", observaciones, unaLinea = false) { observaciones = it }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = nombre.isNotBlank() && tipo.isNotBlank() && region.isNotBlank() && comuna.isNotBlank(),
                    onClick = {
                        vm.crear(
                            Organizacion(
                                nombre = nombre.trim(), tipo = tipo.trim(), rubro = rubro.trim(),
                                comuna = comuna, region = region,
                                sitioWeb = sitioWeb.trim(), observaciones = observaciones.trim()
                            )
                        )
                        dialogo = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { dialogo = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun OrganizacionDetalleScreen(id: Long, alVolver: () -> Unit, alAbrirOportunidad: (Long) -> Unit) {
    val app = app()
    val vm: OrganizacionDetalleViewModel = viewModel(
        factory = vmFactory { OrganizacionDetalleViewModel(app.repo, app.oportunidadRepo, app.sesion, id) }
    )
    val organizacion by vm.organizacion.collectAsState()
    val contactos by vm.contactos.collectAsState()
    val eventos by vm.eventos.collectAsState()
    var dialogo by remember { mutableStateOf(false) }
    var contactoPorEliminar by remember { mutableStateOf<Contacto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = barraColores(),
                title = { Text(organizacion?.nombre ?: "Organización") },
                navigationIcon = { IconButton(onClick = alVolver) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            organizacion?.let { o ->
                item {
                    Column(Modifier.padding(16.dp)) {
                        Text("${o.tipo} · ${o.rubro}")
                        Text("${o.comuna}, ${o.region}")
                        if (o.sitioWeb.isNotBlank()) Text("Sitio web: ${o.sitioWeb}", style = MaterialTheme.typography.bodySmall)
                        if (o.observaciones.isNotBlank()) Text("Obs.: ${o.observaciones}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Titulo("Contactos (${contactos.size})") }
            items(contactos, key = { "c${it.id}" }) { c ->
                Row(Modifier.padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(c.nombre, fontWeight = FontWeight.SemiBold)
                        Text("${c.cargo} · ${c.area}")
                        Text("${c.correo} · ${c.telefono}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Prefiere: ${c.medioPreferente} · ${c.estado}" +
                                (c.fechaUltimoContacto?.let { " · Último contacto: ${it.aFecha()}" } ?: ""),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (c.observaciones.isNotBlank()) Text(c.observaciones, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { contactoPorEliminar = c }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar contacto", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item { TextButton(onClick = { dialogo = true }, modifier = Modifier.padding(horizontal = 8.dp)) { Text("+ Agregar contacto") } }
            item { Titulo("Eventos y oportunidades (${eventos.size})") }
            items(eventos, key = { "e${it.id}" }) { e -> OportunidadCard(e) { alAbrirOportunidad(e.id) } }
        }
    }

    contactoPorEliminar?.let { c ->
        ConfirmarEliminacion(
            titulo = "Eliminar contacto",
            mensaje = "¿Eliminar a ${c.nombre} de los contactos?",
            alCancelar = { contactoPorEliminar = null },
            alConfirmar = {
                vm.eliminarContacto(c)
                contactoPorEliminar = null
            }
        )
    }
    if (dialogo) {
        var nombre by remember { mutableStateOf("") }
        var cargo by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }
        var correo by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var medio by remember { mutableStateOf(MEDIOS_CONTACTO.first()) }
        var estadoContacto by remember { mutableStateOf(ESTADOS_CONTACTO.first()) }
        var observaciones by remember { mutableStateOf("") }
        val errorCorreo = if (correo.isNotBlank() && !correoValido(correo)) "Correo inválido" else null
        AlertDialog(
            onDismissRequest = { dialogo = false },
            title = { Text("Nuevo contacto") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Campo("Nombre", nombre) { nombre = it }
                    Campo("Cargo", cargo) { cargo = it }
                    CampoSelector("Área", AREAS_CONTACTO, area.ifBlank { null }, { it }) { area = it }
                    Campo("Correo (ficticio)", correo, error = errorCorreo) { correo = it }
                    Campo("Teléfono (ficticio)", telefono) { telefono = it }
                    CampoSelector("Medio de contacto preferente", MEDIOS_CONTACTO, medio, { it }) { medio = it }
                    CampoSelector("Estado del contacto", ESTADOS_CONTACTO, estadoContacto, { it }) { estadoContacto = it }
                    Campo("Observaciones", observaciones, unaLinea = false) { observaciones = it }
                }
            },
            confirmButton = {
                TextButton(enabled = nombre.isNotBlank() && area.isNotBlank() && errorCorreo == null, onClick = {
                    vm.agregarContacto(
                        Contacto(
                            organizacionId = 0, nombre = nombre.trim(), cargo = cargo.trim(), area = area,
                            correo = correo.trim(), telefono = telefono.trim(), medioPreferente = medio,
                            estado = estadoContacto,
                            fechaUltimoContacto = if (estadoContacto == ESTADOS_CONTACTO.first()) null else System.currentTimeMillis(),
                            observaciones = observaciones.trim()
                        )
                    )
                    dialogo = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { dialogo = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp)
    )
}