package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelo.Categoria;
import org.junit.jupiter.api.Test;


 // Pruebas unitarias de PlatilloService: las validaciones que fallan antes de consultar la base de datos.
 
public class PlatilloServiceTest {

    private final PlatilloService servicio = new PlatilloService();
    private final Categoria bebidas = new Categoria(1, "Bebidas");

    @Test
    void agregarRechazaSinCategoria() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "25", null));

        assertEquals("Selecciona una categoria", ex.getMessage());
    }

    @Test
    void agregarRechazaUnPrecioQueNoEsNumero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "veinte", bebidas));

        assertEquals("El precio debe ser un numero", ex.getMessage());
    }

    @Test
    void agregarRechazaUnPrecioDeCero() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "0", bebidas));
    }

    @Test
    void agregarRechazaUnPrecioNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "-5", bebidas));
    }

    @Test
    void agregarRechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("   ", "25", bebidas));
    }
}
