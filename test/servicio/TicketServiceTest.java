package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import modelo.Categoria;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mesero;
import modelo.Pedido;
import modelo.Platillo;
import org.junit.jupiter.api.Test;

public class TicketServiceTest {

    private final CajaService caja = new CajaService();
    private final TicketService tickets = new TicketService();

    // Pedido de la mesa 3: 2 tacos de $60 + 1 agua de $35 = $155 (precios con IVA incluido)
    private Pedido pedidoDeEjemplo() {
        Categoria cat = new Categoria(1, "Comida");
        Pedido pedido = new Pedido(new Mesa(3, 3, "Ocupada"), new Mesero(1, 1, "Juan", "Perez", true));
        pedido.setId(15);
        pedido.agregarDetalle(new DetallePedido(new Platillo(1, cat, "Tacos al pastor", 60.0), 2));
        pedido.agregarDetalle(new DetallePedido(new Platillo(2, cat, "Agua de horchata", 35.0), 1));
        return pedido;
    }

    @Test
    void elTotalEsLaSumaDeLosDetalles() {
        assertEquals(155.0, caja.calcularTotal(pedidoDeEjemplo()), 0.001);
    }

    @Test
    void elSubtotalEsElTotalEntreUnoPuntoDieciseis() {
        assertEquals(133.62, caja.calcularSubtotal(pedidoDeEjemplo()), 0.001);
    }

    @Test
    void subtotalMasIvaSiempreDaElTotal() {
        Pedido p = pedidoDeEjemplo();
        assertEquals(caja.calcularTotal(p), caja.calcularSubtotal(p) + caja.calcularIva(p), 0.001);
        assertEquals(21.38, caja.calcularIva(p), 0.001);
    }

    @Test
    void elTicketTraeLosDatosDelCobro() {
        String t = tickets.generar(pedidoDeEjemplo(), "Efectivo", LocalDateTime.of(2026, 11, 23, 22, 30));

        assertTrue(t.contains("LA COMARCA MEXICANA"));
        assertTrue(t.contains("Ticket #15"));
        assertTrue(t.contains("23/11/2026 22:30"));
        assertTrue(t.contains("Mesa: 3"));
        assertTrue(t.contains("2 x Tacos al pastor"));
        assertTrue(t.contains("$120.00"));
        assertTrue(t.contains("Subtotal"));
        assertTrue(t.contains("$133.62"));
        assertTrue(t.contains("IVA (16%)"));
        assertTrue(t.contains("$21.38"));
        assertTrue(t.contains("TOTAL"));
        assertTrue(t.contains("$155.00"));
        assertTrue(t.contains("Pago: Efectivo"));
    }

    @Test
    void elTicketRechazaUnPedidoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> tickets.generar(null, "Efectivo", LocalDateTime.now()));
    }

    @Test
    void elTicketRechazaUnMetodoDePagoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> tickets.generar(pedidoDeEjemplo(), " ", LocalDateTime.now()));
    }
}