package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelo.Rol;
import modelo.Roles;
import modelo.Usuario;
import org.junit.jupiter.api.Test;


 //Pruebas unitarias de UsuarioService: las reglas que fallan antes de consultar la base de datos.
 
public class UsuarioServiceTest {

    private final UsuarioService servicio = new UsuarioService();

    private final Rol rolSuper = new Rol(Roles.SUPER_ADMINISTRADOR, "Super Administrador");
    private final Rol rolMesero = new Rol(Roles.MESERO, "Mesero");

    private final Usuario ariadna = new Usuario(1, "Ariadna Cornejo", "123", rolSuper);
    private final Usuario carlos = new Usuario(3, "Carlos Vallarta", "098", rolMesero);

    // ---------- agregar ----------

    @Test
    void agregarRechazaSinRol() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Nuevo", "111", null));

        assertEquals("Selecciona un rol", ex.getMessage());
    }

    @Test
    void agregarRechazaUnUsuarioVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("   ", "111", rolMesero));
    }

    @Test
    void agregarRechazaUnaContrasenaVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Nuevo", "", rolMesero));
    }

    // ---------- actualizar ----------

    @Test
    void actualizarRechazaSinUsuarioSeleccionado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(null, "Nuevo", "111", rolMesero, ariadna));

        assertEquals("Selecciona un usuario de la tabla", ex.getMessage());
    }

    @Test
    void actualizarRechazaSinRol() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "Carlos", "", null, ariadna));
    }

    @Test
    void nadiePuedeCambiarSuPropioRol() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(ariadna, "Ariadna Cornejo", "", rolMesero, ariadna));

        assertEquals("No puedes cambiar tu propio rol", ex.getMessage());
    }

    @Test
    void actualizarRechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "   ", "", rolMesero, ariadna));
    }

    // ---------- eliminar ----------

    @Test
    void eliminarRechazaSinUsuarioSeleccionado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminar(null, ariadna));

        assertEquals("Selecciona un usuario de la tabla", ex.getMessage());
    }

    @Test
    void nadiePuedeEliminarSuPropiaCuenta() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminar(ariadna, ariadna));

        assertEquals("No puedes eliminar tu propio usuario", ex.getMessage());
    }
}
