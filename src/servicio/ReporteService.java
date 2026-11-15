package servicio;

import dao.ReporteDAO;
import java.sql.SQLException;
import modelo.Reporte;
import modelo.TipoReporte;


// Capa de servicio: decide que consulta ejecutar segun el reporte que se pidio.

public class ReporteService {

    private final ReporteDAO reporteDAO = new ReporteDAO();

    public Reporte generar(TipoReporte tipo) throws SQLException {
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un reporte");
        }

        switch (tipo) {
            case VENTAS_POR_DIA:
                return reporteDAO.ventasPorDia();
            case PLATILLOS_MAS_VENDIDOS:
                return reporteDAO.platillosMasVendidos();
            case VENTAS_POR_MESERO:
                return reporteDAO.ventasPorMesero();
            case INGRESOS_POR_METODO_PAGO:
                return reporteDAO.ingresosPorMetodoPago();
            case PEDIDOS_POR_ESTADO:
                return reporteDAO.pedidosPorEstado();
            default:
                throw new IllegalArgumentException("Reporte no disponible");
        }
    }
}