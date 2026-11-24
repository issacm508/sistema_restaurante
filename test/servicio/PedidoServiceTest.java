package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mesero;
import modelo.Pedido;
import modelo.Platillo;
import modelo.Rol;
import modelo.Roles;
import modelo.Usuario;
import org.junit.jupiter.api.Test;


public class PedidoServiceTest {

    private final PedidoService servicio = new PedidoService();

    private final Usuario usuario = new Usuario(1, "Carlos", "098", new Rol(Roles.MESERO, "Mesero"));
    private final Mesa mesa = new Mesa(1, 1, MesaService.DISPONIBLE);
    private final Platillo agua = new Platillo(1, new Categoria(1, "Bebidas"), "Agua de Horchata", 25.0);

    @Test
    void registrarRechazaUnaMesaNula() {
        List<DetallePedido> detalles = new ArrayList<>();
        detalles.add(new DetallePedido(agua, 1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(usuario, null, detalles));

        assertEquals("Selecciona una mesa", ex.getMessage());
    }

    @Test
    void registrarRechazaUnPedidoSinPlatillos() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(usuario, mesa, new ArrayList<>()));

        assertEquals("Agrega al menos un platillo al pedido", ex.getMessage());
    }

    @Test
    void registrarRechazaUnaListaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(usuario, mesa, null));
    }

    @Test
    void cancelarRechazaUnPedidoNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.cancelar(usuario, null));

        assertEquals("Selecciona un pedido de la tabla", ex.getMessage());
    }

    @Test
    void marcarComoCompletadoRechazaUnPedidoNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.marcarComoCompletado(null));

        assertEquals("Selecciona un pedido de la tabla", ex.getMessage());
    }

    @Test
    void cancelarRechazaAUnMeseroQueNoTomoElPedido() {
        Pedido ajeno = new Pedido();
        ajeno.setId(7);
        ajeno.setMesero(new Mesero(5, 99, "Otro", "Mesero", true));   // pertenece al usuario 99, no al 1

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.cancelar(usuario, ajeno));

        assertEquals("Solo el mesero que tomo el pedido puede cancelarlo", ex.getMessage());
    }

    @Test
    void cancelarRechazaAlPersonalDeCocina() {
        Usuario cocinero = new Usuario(2, "Cocina", "xyz", new Rol(Roles.COCINA, "Cocina"));
        Pedido pedido = new Pedido();
        pedido.setId(8);
        pedido.setMesero(new Mesero(5, 2, "Mismo", "Id", true));   // aunque coincida el id, su rol no puede

        assertThrows(IllegalArgumentException.class, () -> servicio.cancelar(cocinero, pedido));
    }
}