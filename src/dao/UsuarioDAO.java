package dao;

import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Rol;
import modelo.Usuario;

/**
 * Capa DAO: es la UNICA que escribe SQL para la tabla usuarios.
 */
public class UsuarioDAO {

    // La parte comun de las consultas: el usuario junto con su rol
    private static final String SELECT_USUARIO =
            "SELECT u.id, u.username, u.password, r.id AS idRol, r.nombreRol "
          + "FROM usuarios u "
          + "JOIN roles r ON u.idRol = r.id ";

    
      //Busca un usuario por su nombre de usuario (username). Devuelve el Usuario con su Rol, o null si no existe.
  
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = SELECT_USUARIO + "WHERE u.username = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);   // el ? se rellena aqui: evita la inyeccion SQL

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        }
        return null;   // no se encontro ningun usuario con ese username
    }

    //Todos los usuarios, cada uno con su Rol.
    public List<Usuario> listar() throws SQLException {
        String sql = SELECT_USUARIO + "ORDER BY u.id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(crearUsuario(rs));
            }
        }
        return usuarios;
    }

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (idRol, username, password) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuario.getRol().getId());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPassword());
            ps.executeUpdate();
        }
    }

  
    // Actualiza el usuario y su rol. La contrasena solo se cambia si cambiarPassword es true.
    public void actualizar(Usuario usuario, boolean cambiarPassword) throws SQLException {
        String sql = cambiarPassword
                ? "UPDATE usuarios SET idRol = ?, username = ?, password = ? WHERE id = ?"
                : "UPDATE usuarios SET idRol = ?, username = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuario.getRol().getId());
            ps.setString(2, usuario.getUsername());
            if (cambiarPassword) {
                ps.setString(3, usuario.getPassword());
                ps.setInt(4, usuario.getId());
            } else {
                ps.setInt(3, usuario.getId());
            }
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Usuario (con su Rol)
    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        Rol rol = new Rol(rs.getInt("idRol"), rs.getString("nombreRol"));
        return new Usuario(rs.getInt("id"), rs.getString("username"),
                           rs.getString("password"), rol);
    }
}