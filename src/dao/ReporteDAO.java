package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import modelo.Reporte;

//Capa DAO: aqui estan las consultas de los reportes (con JOIN, GROUP BY y funciones de agregacion). Solo lee datos, no modifica nada.
public class ReporteDAO {

    // Cuanto se cobro cada dia. 
    public Reporte ventasPorDia() throws SQLException {
        return ejecutar("Ventas por dia",
                "SELECT DATE(fechaPago) AS Dia, COUNT(*) AS Pagos, SUM(monto) AS Total "
              + "FROM pagos "
              + "GROUP BY DATE(fechaPago) "
              + "ORDER BY Dia DESC");
    }

    // Los 10 platillos que mas se piden (sin contar los pedidos cancelados). 
    public Reporte platillosMasVendidos() throws SQLException {
        return ejecutar("Platillos mas vendidos",
                "SELECT pl.nombre AS Platillo, SUM(d.cantidad) AS Unidades, SUM(d.subtotal) AS Ingresos "
              + "FROM detalle_pedido d "
              + "JOIN platillos pl ON d.idPlatillo = pl.id "
              + "JOIN pedidos p ON d.idPedido = p.id "
              + "WHERE p.idEstado <> 3 "   // 3 = Cancelado
              + "GROUP BY pl.id, pl.nombre "
              + "ORDER BY Unidades DESC "
              + "LIMIT 10");
    }

    //Cuanto cobro cada mesero (solo pedidos ya pagados). 
    public Reporte ventasPorMesero() throws SQLException {
        return ejecutar("Ventas por mesero",
                "SELECT CONCAT(me.nombre, ' ', me.apellido) AS Mesero, COUNT(*) AS Pedidos, SUM(pg.monto) AS Total "
              + "FROM pagos pg "
              + "JOIN pedidos p ON pg.idPedido = p.id "
              + "JOIN meseros me ON p.idMesero = me.id "
              + "GROUP BY me.id, me.nombre, me.apellido "
              + "ORDER BY Total DESC");
    }

    //Cuanto entro por efectivo, tarjeta y transferencia. 
    public Reporte ingresosPorMetodoPago() throws SQLException {
        return ejecutar("Ingresos por metodo de pago",
                "SELECT mp.nombreMetodo AS Metodo, COUNT(*) AS Pagos, SUM(pg.monto) AS Total "
              + "FROM pagos pg "
              + "JOIN metodos_pago mp ON pg.idMetodoPago = mp.id "
              + "GROUP BY mp.id, mp.nombreMetodo "
              + "ORDER BY Total DESC");
    }

    //Cuantos pedidos hay En Proceso, Completados y Cancelados. 

    public Reporte pedidosPorEstado() throws SQLException {
        // LEFT JOIN: tambien aparecen los estados que todavia no tienen ningun pedido (con 0)
        return ejecutar("Pedidos por estado",
                "SELECT ep.nombreEstado AS Estado, COUNT(p.id) AS Pedidos "
              + "FROM estados_pedido ep "
              + "LEFT JOIN pedidos p ON p.idEstado = ep.id "
              + "GROUP BY ep.id, ep.nombreEstado "
              + "ORDER BY ep.id");
    }

      //Ejecuta una consulta y la convierte en un Reporte.Los nombres de columna salen de los alias (AS) de cada consulta.

    private Reporte ejecutar(String titulo, String sql) throws SQLException {
        Reporte reporte = new Reporte(titulo);

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int numColumnas = meta.getColumnCount();

            for (int i = 1; i <= numColumnas; i++) {
                reporte.agregarColumna(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] fila = new Object[numColumnas];
                for (int i = 1; i <= numColumnas; i++) {
                    fila[i - 1] = rs.getObject(i);
                }
                reporte.agregarFila(fila);
            }
        }
        return reporte;
    }
}