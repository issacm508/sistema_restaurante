package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import modelo.Categoria;
import modelo.Platillo;
import org.junit.jupiter.api.Test;

public class PlatilloServiceTest {

    private final PlatilloService servicio = new PlatilloService();
    private final Categoria bebidas = new Categoria(1, "Bebidas");
    private final Platillo agua = new Platillo(1, bebidas, "Agua de Horchata", 25);

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

    @Test
    void agregarRechazaNaNComoPrecio() {
        // "NaN" se puede convertir a double, pero no es un precio valido
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "NaN", bebidas));

        assertEquals("El precio debe ser un numero", ex.getMessage());
    }

    @Test
    void agregarRechazaUnPrecioMasGrandeQueElMaximo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("Agua", "10000", bebidas));

        assertEquals("El precio no puede ser mayor a 9999.99", ex.getMessage());
    }

    @Test
    void actualizarRechazaSinPlatilloSeleccionado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(null, "Agua", "25", bebidas));

        assertEquals("Selecciona un platillo de la tabla", ex.getMessage());
    }

    @Test
    void actualizarRechazaSinCategoria() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(agua, "Agua", "25", null));

        assertEquals("Selecciona una categoria", ex.getMessage());
    }

    @Test
    void actualizarRechazaUnPrecioQueNoEsNumero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(agua, "Agua", "abc", bebidas));

        assertEquals("El precio debe ser un numero", ex.getMessage());
    }

    @Test
    void eliminarRechazaSinPlatilloSeleccionado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminar(null));

        assertEquals("Selecciona un platillo de la tabla", ex.getMessage());
    }
}
