package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


 //Pruebas unitarias de AuthService: las validaciones del login que fallan antes de consultar la base de datos.

public class AuthServiceTest {

    private final AuthService auth = new AuthService();

    @Test
    void rechazaUsuarioNulo() {
        assertThrows(IllegalArgumentException.class, () -> auth.login(null, "123"));
    }

    @Test
    void rechazaUsuarioVacio() {
        assertThrows(IllegalArgumentException.class, () -> auth.login("   ", "123"));
    }

    @Test
    void rechazaContrasenaNula() {
        assertThrows(IllegalArgumentException.class, () -> auth.login("Carlos", null));
    }

    @Test
    void rechazaContrasenaVacia() {
        assertThrows(IllegalArgumentException.class, () -> auth.login("Carlos", ""));
    }

    @Test
    void elMensajeEsClaro() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> auth.login("", ""));

        assertEquals("Escribe tu usuario y tu contrasena", ex.getMessage());
    }
}
