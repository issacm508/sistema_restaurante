package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Mesero;

public class MeseroDAO {

    // En la tabla estados_empleado, el id 1 es "Activo"
    private static final int ESTADO_ACTIVO = 1;

  
      
    //Busca al mesero que corresponde a un usuario (login).Devuelve null si ese usuario no esta registrado como mesero.

    public Mesero buscarPorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT id, idUsuario, idEstado, nombre, apellido "
                   + "FROM meseros WHERE idUsuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean activo = rs.getInt("idEstado") == ESTADO_ACTIVO;
                    return new Mesero(rs.getInt("id"), rs.getInt("idUsuario"),
                            rs.getString("nombre"), rs.getString("apellido"), activo);
                }
            }
        }
        return null;   // ese usuario no es mesero
    }


    public void insertar(int idUsuario, String nombre, String apellido) throws SQLException {
        String sql = "INSERT INTO meseros (idUsuario, idEstado, nombre, apellido) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, ESTADO_ACTIVO);
            ps.setString(3, nombre);
            ps.setString(4, apellido);
            ps.executeUpdate();
        }
    }
}