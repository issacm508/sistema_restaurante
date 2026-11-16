package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

//Pruebas unitarias de la clase Platillo: comprueban que las validaciones funcionan.No usan la base de datos.

public class PlatilloTest {

    private final Categoria bebidas = new Categoria(1, "Bebidas");

    @Test
    void creaUnPlatilloValido() {
        Platillo p = new Platillo(1, bebidas, "Agua de Horchata", 25.0);

        assertEquals("Agua de Horchata", p.getNombre());
        assertEquals(25.0, p.getPrecio(), 0.001);
        assertEquals("Bebidas", p.getCategoria().getNombre());
    }

    @Test
    void quitaLosEspaciosDelNombre() {
        Platillo p = new Platillo(1, bebidas, "  Flan Napolitano  ", 45.0);

        assertEquals("Flan Napolitano", p.getNombre());
    }

    @Test
    void rechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Platillo(1, bebidas, "   ", 25.0));
    }

    @Test
    void rechazaUnNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Platillo(1, bebidas, null, 25.0));
    }

    @Test
    void rechazaUnPrecioDeCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Platillo(1, bebidas, "Refresco", 0));
    }

    @Test
    void rechazaUnPrecioNegativoConUnMensajeClaro() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Platillo(1, bebidas, "Refresco", -5));

        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }
}
