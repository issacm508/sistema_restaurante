package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Rol;


//Capa DAO: es la UNICA que escribe SQL para la tabla roles.

public class RolDAO {

    public List<Rol> listar() throws SQLException {
        String sql = "SELECT id, nombreRol FROM roles ORDER BY id";
        List<Rol> roles = new ArrayList<>();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Rol(rs.getInt("id"), rs.getString("nombreRol")));
            }
        }
        return roles;
    }
}
