package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


 // Pruebas unitarias de DetallePedido: el subtotal se calcula y las validaciones funcionan.
public class DetallePedidoTest {

    private final Platillo agua = new Platillo(1, new Categoria(1, "Bebidas"), "Agua de Horchata", 25.0);

    @Test
    void elSubtotalEsPrecioPorCantidad() {
        DetallePedido detalle = new DetallePedido(agua, 3);

        assertEquals(75.0, detalle.getSubtotal(), 0.001);
    }

    @Test
    void rechazaUnaCantidadDeCero() {
        assertThrows(IllegalArgumentException.class, () -> new DetallePedido(agua, 0));
    }

    @Test
    void rechazaUnaCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new DetallePedido(agua, -2));
    }

    @Test
    void rechazaUnPlatilloNulo() {
        assertThrows(IllegalArgumentException.class, () -> new DetallePedido(null, 1));
    }
}
