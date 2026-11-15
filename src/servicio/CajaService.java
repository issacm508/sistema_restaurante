package servicio;

import dao.PagoDAO;
import dao.PedidoDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import modelo.DetallePedido;
import modelo.MetodoPago;
import modelo.Pago;
import modelo.Pedido;


 // Capa de servicio de Caja: valida y coordina el cobro de pedidos.
 
public class CajaService {

    private static final int ESTADO_COMPLETADO = 2;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    // Pedidos Completados (listos desde cocina) que todavia no tienen pago
    public List<Pedido> listarPorCobrar() throws SQLException {
        Set<Integer> pagados = pagoDAO.listarIdsPedidosPagados();
        List<Pedido> porCobrar = new ArrayList<>();

        for (Pedido pedido : pedidoDAO.listarPorEstado(ESTADO_COMPLETADO)) {
            if (!pagados.contains(pedido.getId())) {
                porCobrar.add(pedido);
            }
        }
        return porCobrar;
    }

    public List<MetodoPago> listarMetodosPago() throws SQLException {
        return pagoDAO.listarMetodosPago();
    }

    // El total es la suma de los subtotales de cada detalle del pedido
    public double calcularTotal(Pedido pedido) {
        double total = 0;
        for (DetallePedido detalle : pedido.getDetalles()) {
            total += detalle.getSubtotal();
        }
        return Math.round(total * 100.0) / 100.0;   // redondea a 2 decimales
    }

    // Cobra el pedido: registra el pago y libera la mesa
    public void cobrar(Pedido pedido, MetodoPago metodo) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("Selecciona un pedido");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Selecciona un metodo de pago");
        }

        double total = calcularTotal(pedido);
        if (total <= 0) {
            throw new IllegalArgumentException("El pedido no tiene un total valido");
        }

        Pago pago = new Pago(pedido.getId(), total, metodo);
        pagoDAO.registrarCobro(pago, pedido.getMesa().getId());
    }
}