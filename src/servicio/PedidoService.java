package servicio;

import dao.MeseroDAO;
import dao.PedidoDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.DetallePedido;
import modelo.EstadosPedido;
import modelo.Mesa;
import modelo.Mesero;
import modelo.Pedido;
import modelo.Roles;
import modelo.Usuario;

/**
 * Capa de servicio: reglas de negocio de los pedidos.
 */
public class PedidoService {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MeseroDAO meseroDAO = new MeseroDAO();
    private final MesaService mesaService = new MesaService();

    /** Solo las mesas libres se pueden usar para un pedido nuevo. */
    public List<Mesa> obtenerMesasDisponibles() throws SQLException {
        List<Mesa> disponibles = new ArrayList<>();
        for (Mesa m : mesaService.obtenerTodas()) {
            if (MesaService.DISPONIBLE.equals(m.getEstado())) {
                disponibles.add(m);
            }
        }
        return disponibles;
    }

    public Pedido registrar(Usuario usuario, Mesa mesa, List<DetallePedido> detalles) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("Selecciona una mesa");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un platillo al pedido");
        }

        // Regla: el pedido lo toma un mesero. El usuario del login debe estar registrado como mesero.
        Mesero mesero = meseroDAO.buscarPorIdUsuario(usuario.getId());

        // Excepcion: el Super Administrador tiene acceso a todo. Si todavia no es mesero,
        // se registra automaticamente la primera vez que toma un pedido.
        if (mesero == null && usuario.getRol().getId() == Roles.SUPER_ADMINISTRADOR) {
            meseroDAO.insertar(usuario.getId(), usuario.getUsername(), "(Super Admin)");
            mesero = meseroDAO.buscarPorIdUsuario(usuario.getId());
        }

        if (mesero == null) {
            throw new IllegalArgumentException("Tu usuario no esta registrado como mesero, no puede tomar pedidos");
        }
        if (!mesero.isActivo()) {
            throw new IllegalArgumentException("Tu cuenta de mesero esta inactiva");
        }

        // Regla: la mesa debe seguir libre. Se vuelve a consultar por si otra persona la ocupo mientras tanto.
        for (Mesa actual : mesaService.obtenerTodas()) {
            if (actual.getId() == mesa.getId() && !MesaService.DISPONIBLE.equals(actual.getEstado())) {
                throw new IllegalArgumentException("La mesa " + mesa.getNumeroMesa() + " ya no esta disponible");
            }
        }

        Pedido pedido = new Pedido(mesa, mesero);
        for (DetallePedido d : detalles) {
            pedido.agregarDetalle(d);
        }

        pedidoDAO.registrar(pedido);
        return pedido;
    }

    // Los pedidos que la cocina todavia tiene que preparar. 
    public List<Pedido> obtenerPedidosEnProceso() throws SQLException {
        return pedidoDAO.listarPorEstado(EstadosPedido.EN_PROCESO);
    }

    // La cocina marca un pedido como listo (Completado). 
    public void marcarComoCompletado(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("Selecciona un pedido de la tabla");
        }

        // Regla: solo se puede completar un pedido que siga En Proceso
        // (otra persona de la cocina pudo haberlo completado ya).
        if (!sigueEnProceso(pedido)) {
            throw new IllegalArgumentException("El pedido #" + pedido.getId() + " ya no esta en proceso");
        }

        pedidoDAO.actualizarEstado(pedido.getId(), EstadosPedido.COMPLETADO);
    }

    // Cancela un pedido En Proceso y deja libre su mesa. 
    public void cancelar(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("Selecciona un pedido de la tabla");
        }

        // Regla: solo se puede cancelar un pedido que siga En Proceso
        if (!sigueEnProceso(pedido)) {
            throw new IllegalArgumentException("El pedido #" + pedido.getId() + " ya no esta en proceso");
        }

        pedidoDAO.cancelar(pedido);
    }

    // Vuelve a consultar la base de datos, por si otra persona cambio el pedido mientras tanto
    private boolean sigueEnProceso(Pedido pedido) throws SQLException {
        for (Pedido p : pedidoDAO.listarPorEstado(EstadosPedido.EN_PROCESO)) {
            if (p.getId() == pedido.getId()) {
                return true;
            }
        }
        return false;
    }
}