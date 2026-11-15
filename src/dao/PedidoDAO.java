package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import modelo.DetallePedido;
import modelo.Pedido;

/**
 * Capa DAO: es la UNICA que escribe SQL para pedidos y detalle_pedido.
 */
public class PedidoDAO {

    private static final String MESA_OCUPADA = "Ocupada";

    /**
     * Guarda el pedido, sus detalles y marca la mesa como ocupada.
     * Se hace en UNA TRANSACCION: o se guarda todo, o no se guarda nada.
     * Asi nunca queda un pedido sin detalles, ni una mesa ocupada sin pedido.
     */
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
}
