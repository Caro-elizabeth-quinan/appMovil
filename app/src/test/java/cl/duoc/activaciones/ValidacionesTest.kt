package cl.duoc.activaciones

import cl.duoc.activaciones.model.UsuarioUiState
import cl.duoc.activaciones.model.validarRegistro
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Validaciones del formulario de registro (Guía 11). */
class ValidacionesTest {

    private val valido = UsuarioUiState(
        nombre = "Camila Rojas",
        correo = "camila@demo.cl",
        clave = "clave123",
        confirmarClave = "clave123",
        direccion = "Av. Ficticia 123",
        area = "Comercial",
        zona = "Metropolitana",
        aceptaTerminos = true
    )

    @Test
    fun `formulario completo no tiene errores`() {
        assertFalse(validarRegistro(valido).hayErrores)
    }

    @Test
    fun `formulario vacio marca todos los campos`() {
        val e = validarRegistro(UsuarioUiState())
        assertTrue(e.hayErrores)
        assertNotNull(e.nombre)
        assertNotNull(e.correo)
        assertNotNull(e.clave)
        assertNotNull(e.direccion)
        assertNotNull(e.terminos)
    }

    @Test
    fun `correo sin dominio es invalido`() {
        assertNotNull(validarRegistro(valido.copy(correo = "camila@")).correo)
    }

    @Test
    fun `claves distintas no coinciden`() {
        assertEquals("Las contraseñas no coinciden", validarRegistro(valido.copy(confirmarClave = "otra123")).confirmarClave)
    }

    @Test
    fun `clave sin numeros es rechazada`() {
        assertNotNull(validarRegistro(valido.copy(clave = "abcdefg", confirmarClave = "abcdefg")).clave)
    }
}
