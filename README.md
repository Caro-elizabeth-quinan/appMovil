# ActivacionesApp — DSY1105 (007D)

App Android (Kotlin + Jetpack Compose) para gestión móvil de oportunidades y activaciones corporativas.

**Integrantes:** Raysa Aguilera y Carolina Quinan · Sección 007D · Docente: Vicente Zapata
Paleta rosa / morado (`ui/theme/Theme.kt`).

## Cómo ejecutarla
1. Clona el repositorio y abre la carpeta `AppMovilesAvance` en Android Studio (Koala 2024.1 o superior).
2. Espera el Gradle Sync (la primera vez descarga Gradle 8.7 y dependencias).
3. Run ▶ en emulador o equipo con Android 8.0+ (API 26).

Usuarios de prueba (clave `1234`): `ejecutivo@demo.cl`, `ejecutivo2@demo.cl`, `supervisor@demo.cl`.
Datos ficticios cargados en el primer arranque.

## Flujo de navegación
`MainActivity` → `AppNavigation` → Login → Menú → Oportunidades / Alertas / Organizaciones / Perfil
Registro (Guía 11): Login → `RegistroScreen` → `ResumenScreen` (mismo `UsuarioViewModel`, sin pasar argumentos).

## Estructura (MVVM)
```
cl.duoc.activaciones/
├── data/
│   ├── local/        AppDatabase (Room v5 + migraciones), entidades y DAOs
│   └── repository/   OportunidadRepository, Repositorio
├── model/            Usuario, Organización, Contacto, Seguimiento, enums del caso,
│                     UsuarioUiState, UsuarioErrores, Validaciones, Logica (filtros y alertas)
├── navigation/       AppNavigation (NavHost + rutas)
├── ui/
│   ├── components/   Campo, CampoSelector, Selector, tarjetas
│   ├── screen/       Login, Registro, Resumen, Menú, Lista, Formulario, Detalle,
│   │                 Alertas, Organizaciones, Perfil, QR
│   └── theme/        Theme.kt
├── util/             Sesión, fechas, ubicación, cámara, QR, notificaciones (WorkManager)
├── viewmodel/        UsuarioViewModel (registro), PrincipalViewModel (sesión/alertas),
│                     OportunidadViewModel, DetalleViewModel, Organizacion*ViewModel, LoginViewModel
├── ActivacionesApp.kt
└── MainActivity.kt
```
Vista → ViewModel (UiState + StateFlow, validaciones) → Repository → DAO → Room.

## Requisitos de la primera entrega
| Criterio | Dónde |
|---|---|
| Lista de oportunidades almacenadas | `ListaOportunidadesScreen` (nombre, tipo, prioridad, estado) |
| Crear / editar | `OportunidadFormScreen` (mismo formulario, id 0 = nueva) |
| Eliminar (con confirmación) | Oportunidades (lista y detalle), seguimientos, organizaciones y contactos |
| Validaciones con error visible | `OportunidadViewModel.guardar()` → `isError` + `supportingText` |
| UiState + StateFlow | `OportunidadUiState`, `uiState: StateFlow` |
| Persistencia Room | `AppDatabase` (sobrevive al reinicio) |
| Reactividad | `OportunidadDao.todas(): Flow` |
| Recurso nativo | Ubicación (LocationManager), cámara (foto de evidencia) y QR con permisos en tiempo de ejecución |
| Formulario + ViewModel compartido (Guía 11) | `RegistroScreen` → `ResumenScreen` con `UsuarioViewModel` |
| Asignar responsable | Supervisor elige el ejecutivo en `OportunidadFormScreen` |
| Filtros | Texto, estado, prioridad, tipo, región, fecha, organización y responsable |
| Íconos en validaciones | `Campo`, `CampoSelector` y `RegistroScreen` muestran un ícono de advertencia en cada campo con error |
| Animaciones | Transiciones entre pantallas (`AppNavigation`), aviso de error con `AnimatedVisibility`, color animado del estado (`EstadoChip`) y reacomodo animado de la lista (`animateItemPlacement`) |

Campos obligatorios: nombre, tipo, descripción, fuente, prioridad, potencial (Alto/Medio/Bajo) y estado
(por defecto "Evento identificado"). Observaciones y datos del evento son opcionales.

## Probar la ubicación en el emulador
Extended controls (⋯) → Location → fija un punto → Set location, y luego "Obtener referencia geográfica".

Tests: `./gradlew test` (lógica de alertas, filtros, visibilidad por perfil y validaciones del registro).
