package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mesero;
import modelo.Pedido;
import modelo.Platillo;

public class PedidoDAO {

    private static final String MESA_OCUPADA = "Ocupada";

     //Guarda el pedido, sus detalles y marca la mesa como ocupada. Se hace en UNA TRANSACCION: o se guarda todo, o no se guarda nada.Asi nunca queda un pedido sin detalles, ni una mesa ocupada sin pedido.
    public void registrar(Pedido pedido) throws SQLException {
        // idEstado (1 = En Proceso) y fechaHora los pone la base de datos con su valor por defecto
        String sqlPedido = "INSERT INTO pedidos (idMesero, idMesa) VALUES (?, ?)";
        String sqlDetalle = "INSERT INTO detalle_pedido (idPedido, idPlatillo, cantidad, subtotal) "
                          + "VALUES (?, ?, ?, ?)";
        String sqlMesa = "UPDATE mesas SET estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);   // inicia la transaccion

            try {
                // 1) insertar el pedido y obtener el id que genero la base de datos
                int idPedido;
                try (PreparedStatement ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getMesero().getId());
                    ps.setInt(2, pedido.getMesa().getId());
                    ps.executeUpdate();

                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se pudo obtener el id del pedido");
                        }
                        idPedido = claves.getInt(1);
                    }
                }

                // 2) insertar cada detalle del pedido
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (DetallePedido d : pedido.getDetalles()) {
                        ps.setInt(1, idPedido);
                        ps.setInt(2, d.getPlatillo().getId());
                        ps.setInt(3, d.getCantidad());
                        ps.setDouble(4, d.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // 3) marcar la mesa como ocupada
                try (PreparedStatement ps = con.prepareStatement(sqlMesa)) {
                    ps.setString(1, MESA_OCUPADA);
                    ps.setInt(2, pedido.getMesa().getId());
                    ps.executeUpdate();
                }

                con.commit();            // todo salio bien: se confirma
                pedido.setId(idPedido);

            } catch (SQLException e) {
                con.rollback();          // algo fallo: se deshace todo
                throw e;
            }
        }
    }

      // Devuelve los pedidos que estan en un estado (por ejemplo, En Proceso), cada uno con su mesa, su mesero y sus detalles. Los mas antiguos primero.

    public List<Pedido> listarPorEstado(int idEstado) throws SQLException {
        String sql = "SELECT p.id AS idPedido, p.fechaHora, "
                   + "       m.id AS idMesa, m.numeroMesa, m.estado AS estadoMesa, "
                   + "       me.id AS idMesero, me.nombre AS nombreMesero, me.apellido AS apellidoMesero, "
                   + "       d.id AS idDetalle, d.cantidad, "
                   + "       pl.id AS idPlatillo, pl.nombre AS nombrePlatillo, pl.precio "
                   + "FROM pedidos p "
                   + "JOIN mesas m ON p.idMesa = m.id "
                   + "JOIN meseros me ON p.idMesero = me.id "
                   + "JOIN detalle_pedido d ON d.idPedido = p.id "
                   + "JOIN platillos pl ON d.idPlatillo = pl.id "
                   + "WHERE p.idEstado = ? "
                   + "ORDER BY p.fechaHora, p.id";

        // El mapa agrupa las filas: cada fila trae UN detalle, y varias filas pertenecen al mismo pedido
        Map<Integer, Pedido> pedidos = new LinkedHashMap<>();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("idPedido");
                    Pedido pedido = pedidos.get(idPedido);

                    if (pedido == null) {   // primera fila de este pedido: se crea el Pedido
                        Mesa mesa = new Mesa(rs.getInt("idMesa"), rs.getInt("numeroMesa"), rs.getString("estadoMesa"));

                        Mesero mesero = new Mesero();
                        mesero.setId(rs.getInt("idMesero"));
                        mesero.setNombre(rs.getString("nombreMesero"));
                        mesero.setApellido(rs.getString("apellidoMesero"));

                        pedido = new Pedido();
                        pedido.setId(idPedido);
                        pedido.setMesa(mesa);
                        pedido.setMesero(mesero);
                        Timestamp fecha = rs.getTimestamp("fechaHora");
                        if (fecha != null) {
                            pedido.setFechaHora(fecha.toLocalDateTime());
                        }
                        pedidos.put(idPedido, pedido);
                    }

                    // en cada fila viene un detalle del pedido
                    Platillo platillo = new Platillo(rs.getInt("idPlatillo"), null,
                            rs.getString("nombrePlatillo"), rs.getDouble("precio"));
                    DetallePedido detalle = new DetallePedido(platillo, rs.getInt("cantidad"));
                    detalle.setId(rs.getInt("idDetalle"));
                    pedido.agregarDetalle(detalle);
                }
            }
        }
        return new ArrayList<>(pedidos.values());
    }

    public void actualizarEstado(int idPedido, int idEstado) throws SQLException {
        String sql = "UPDATE pedidos SET idEstado = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }
}

