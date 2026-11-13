package servicio;
 
import dao.UsuarioDAO;
import java.sql.SQLException;
import modelo.Usuario;

public class AuthService {
 
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Escribe tu usuario y tu contrasena");
        }
 
        Usuario usuario = usuarioDAO.buscarPorUsername(username.trim());
 
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Usuario o contrasena incorrectos");
        }
 
        return usuario;
    }
}