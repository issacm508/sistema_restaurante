package dao;
 
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Mesa;
 
public class MesaDAO {
 
    public List<Mesa> listar() throws SQLException {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT id, numeroMesa, estado FROM mesas ORDER BY numeroMesa";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                lista.add(new Mesa(rs.getInt("id"), rs.getInt("numeroMesa"), rs.getString("estado")));
            }
        }
        return lista;
    }
 
    public void insertar(Mesa mesa) throws SQLException {
        String sql = "INSERT INTO mesas (numeroMesa, estado) VALUES (?, ?)";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, mesa.getNumeroMesa());
            ps.setString(2, mesa.getEstado());
            ps.executeUpdate();
        }
    }
 
    public void actualizarEstado(int id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE mesas SET estado = ? WHERE id = ?";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}