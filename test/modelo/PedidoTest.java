package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

 // Pruebas unitarias de Pedido: estado inicial, calculo del total y validaciones.

public class PedidoTest {

    private final Mesa mesa = new Mesa(1, 1, "Disponible");
    private final Mesero mesero = new Mesero(1, 3, "Carlos", "Vallarta", true);

    private final Categoria bebidas = new Categoria(1, "Bebidas");
    private final Categoria fuertes = new Categoria(2, "Platillos Fuertes");
    private final Platillo agua = new Platillo(1, bebidas, "Agua de Horchata", 25.0);
    private final Platillo milanesa = new Platillo(2, fuertes, "Milanesa de Res", 120.0);

    @Test
    void unPedidoNuevoEmpiezaEnProceso() {
        Pedido pedido = new Pedido(mesa, mesero);

        assertEquals(Pedido.EN_PROCESO, pedido.getEstado());
        assertNotNull(pedido.getFechaHora());
    }

    @Test
    void calculaElTotalSumandoLosDetalles() {
        Pedido pedido = new Pedido(mesa, mesero);
        pedido.agregarDetalle(new DetallePedido(agua, 2));       // 2 x 25  =  50
        pedido.agregarDetalle(new DetallePedido(milanesa, 1));   // 1 x 120 = 120

        assertEquals(170.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    void unPedidoSinDetallesTieneTotalCero() {
        Pedido pedido = new Pedido(mesa, mesero);

        assertEquals(0.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    void rechazaUnPedidoSinMesa() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, mesero));
    }

    @Test
    void rechazaUnPedidoSinMesero() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(mesa, null));
    }

    @Test
    void laListaDeDetallesNoSePuedeModificarDesdeAfuera() {
        Pedido pedido = new Pedido(mesa, mesero);

        assertThrows(UnsupportedOperationException.class,
                () -> pedido.getDetalles().add(new DetallePedido(agua, 1)));
    }
}
