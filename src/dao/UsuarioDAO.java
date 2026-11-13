package dao;
 
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Rol;
import modelo.Usuario;
 
public class UsuarioDAO {
 
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password, "
                   + "       r.id AS idRol, r.nombreRol "
                   + "FROM usuarios u "
                   + "JOIN roles r ON u.idRol = r.id "
                   + "WHERE u.username = ?";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setString(1, username);   
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol(rs.getInt("idRol"), rs.getString("nombreRol"));
                    return new Usuario(rs.getInt("id"), rs.getString("username"),
                                       rs.getString("password"), rol);
                }
            }
        }
        return null;   
    }
}