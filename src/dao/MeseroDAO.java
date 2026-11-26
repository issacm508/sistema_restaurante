package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Mesero;

public class MeseroDAO {

    private static final int ESTADO_ACTIVO = 1;
    private static final int ESTADO_INACTIVO = 2;

    private static final String COLUMNAS = "id, idUsuario, idEstado, nombre, apellido, telefono";

    public Mesero buscarPorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM meseros WHERE idUsuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearMesero(rs);
                }
            }
        }
        return null; 
    }

    public List<Mesero> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM meseros ORDER BY nombre, apellido";
        List<Mesero> meseros = new ArrayList<>();

        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                meseros.add(crearMesero(rs));
            }
        }
        return meseros;
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

    public void actualizar(Mesero mesero) throws SQLException {
        String sql = "UPDATE meseros SET nombre = ?, apellido = ?, telefono = ?, idEstado = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mesero.getNombre());
            ps.setString(2, mesero.getApellido());
            ps.setString(3, mesero.getTelefono());  
            ps.setInt(4, mesero.isActivo() ? ESTADO_ACTIVO : ESTADO_INACTIVO);
            ps.setInt(5, mesero.getId());
            ps.executeUpdate();
        }
    }

    private Mesero crearMesero(ResultSet rs) throws SQLException {
        boolean activo = rs.getInt("idEstado") == ESTADO_ACTIVO;
        return new Mesero(rs.getInt("id"), rs.getInt("idUsuario"),
                rs.getString("nombre"), rs.getString("apellido"),
                rs.getString("telefono"), activo);
    }
}