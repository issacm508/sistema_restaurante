package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.MetodoPago;
import modelo.Pago;

public class PagoDAO {

    private static final String MESA_DISPONIBLE = "Disponible";

    // Devuelve los metodos de pago (Efectivo, Tarjeta de Credito, etc.)
    public List<MetodoPago> listarMetodosPago() throws SQLException {
        String sql = "SELECT id, nombreMetodo FROM metodos_pago ORDER BY id";
        List<MetodoPago> metodos = new ArrayList<>();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                metodos.add(new MetodoPago(rs.getInt("id"), rs.getString("nombreMetodo")));
            }
        }
        return metodos;
    }

    // Devuelve los ids de los pedidos que ya fueron cobrados
    public Set<Integer> listarIdsPedidosPagados() throws SQLException {
        String sql = "SELECT idPedido FROM pagos";
        Set<Integer> ids = new HashSet<>();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("idPedido"));
            }
        }
        return ids;
    }

    // Registra el pago y libera la mesa. Se hace en UNA TRANSACCION: o se guarda todo, o no se guarda nada.
    // Asi nunca queda un pago registrado con la mesa todavia ocupada.
    public void registrarCobro(Pago pago, int idMesa) throws SQLException {
        // fechaPago la pone la base de datos con su valor por defecto
        String sqlPago = "INSERT INTO pagos (idPedido, monto, idMetodoPago) VALUES (?, ?, ?)";
        String sqlMesa = "UPDATE mesas SET estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);   // inicia la transaccion

            try {
                // 1) insertar el pago
                int idPago;
                try (PreparedStatement ps = con.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pago.getIdPedido());
                    ps.setDouble(2, pago.getMonto());
                    ps.setInt(3, pago.getMetodoPago().getId());
                    ps.executeUpdate();

                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se pudo obtener el id del pago");
                        }
                        idPago = claves.getInt(1);
                    }
                }

                // 2) liberar la mesa
                try (PreparedStatement ps = con.prepareStatement(sqlMesa)) {
                    ps.setString(1, MESA_DISPONIBLE);
                    ps.setInt(2, idMesa);
                    ps.executeUpdate();
                }

                con.commit();            // todo salio bien: se confirma
                pago.setId(idPago);

            } catch (SQLException e) {
                con.rollback();          // algo fallo: se deshace todo

                // 1062 = valor duplicado: idPedido es UNICO en la tabla pagos
                if (e.getErrorCode() == 1062) {
                    throw new SQLException("Este pedido ya fue cobrado", e);
                }
                throw e;
            }
        }
    }
}
