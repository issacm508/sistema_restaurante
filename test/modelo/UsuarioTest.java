package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

// Pruebas unitarias de Usuario: el usuario y la contrasena no pueden estar vacios.

public class UsuarioTest {

    private final Rol mesero = new Rol(Roles.MESERO, "Mesero");

    @Test
    void creaUnUsuarioValidoConSuRol() {
        Usuario usuario = new Usuario(1, "Carlos Vallarta", "098", mesero);

        assertEquals("Carlos Vallarta", usuario.getUsername());
        assertEquals(Roles.MESERO, usuario.getRol().getId());
    }

    @Test
    void quitaLosEspaciosDelNombreDeUsuario() {
        Usuario usuario = new Usuario(1, "  Carlos  ", "098", mesero);

        assertEquals("Carlos", usuario.getUsername());
    }

    @Test
    void rechazaUnUsuarioVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(1, "   ", "098", mesero));
    }

    @Test
    void rechazaUnaContrasenaVacia() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(1, "Carlos", "", mesero));
    }
}
