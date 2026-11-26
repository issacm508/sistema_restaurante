package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelo.Mesero;
import org.junit.jupiter.api.Test;

public class MeseroServiceTest {

    private final MeseroService servicio = new MeseroService();
    private final Mesero carlos = new Mesero(1, 3, "Carlos", "Vallarta", "5512345678", true);

    @Test
    void actualizarRechazaSinMeseroSeleccionado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(null, "Carlos", "Vallarta", "5512345678", true));

        assertEquals("Selecciona un mesero de la tabla", ex.getMessage());
    }

    @Test
    void actualizarRechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "   ", "Vallarta", "5512345678", true));
    }

    @Test
    void actualizarRechazaUnApellidoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "Carlos", "", "5512345678", true));
    }

    @Test
    void actualizarRechazaUnTelefonoConLetras() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "Carlos", "Vallarta", "55abc", true));

        assertEquals("El telefono debe tener 10 digitos", ex.getMessage());
    }

    @Test
    void actualizarRechazaUnTelefonoDeLongitudIncorrecta() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(carlos, "Carlos", "Vallarta", "551234567", true));
    }

    @Test
    void elTelefonoValidoSeGuardaSinEspacios() {
        Mesero m = new Mesero();
        m.setTelefono(" 5512345678 ");

        assertEquals("5512345678", m.getTelefono());
    }

    @Test
    void elTelefonoVacioSeGuardaComoNulo() {
        Mesero m = new Mesero(1, 3, "Carlos", "Vallarta", "5512345678", true);
        m.setTelefono("  ");

        assertEquals(null, m.getTelefono());
    }
}