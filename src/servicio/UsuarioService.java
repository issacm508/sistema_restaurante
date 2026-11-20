package servicio;

import dao.RolDAO;
import dao.UsuarioDAO;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import modelo.Cifrado;
import modelo.Rol;
import modelo.Usuario;


public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();

    public List<Usuario> obtenerTodos() throws SQLException {
        return usuarioDAO.listar();
    }

    public List<Rol> obtenerRoles() throws SQLException {
        return rolDAO.listar();
    }

    public void agregar(String username, String password, Rol rol) throws SQLException {
        if (rol == null) {
            throw new IllegalArgumentException("Selecciona un rol");
        }

        // El constructor de Usuario valida que usuario y contrasena no esten vacios
        Usuario nuevo = new Usuario(0, username, password, rol);

        // Regla: no puede haber dos usuarios con el mismo nombre
        if (usuarioDAO.buscarPorUsername(nuevo.getUsername()) != null) {
            throw new IllegalArgumentException("Ya existe el usuario " + nuevo.getUsername());
        }

        // En la base de datos se guarda el hash, nunca la contrasena escrita
        nuevo.setPassword(Cifrado.sha256(password));
        usuarioDAO.insertar(nuevo);
    }

    public void actualizar(Usuario seleccionado, String username, String password,
                           Rol rol, Usuario actual) throws SQLException {
        if (seleccionado == null) {
            throw new IllegalArgumentException("Selecciona un usuario de la tabla");
        }
        if (rol == null) {
            throw new IllegalArgumentException("Selecciona un rol");
        }

        // Regla: nadie puede quitarse a si mismo el rol (se quedaria sin acceso a esta pantalla)
        if (actual != null && seleccionado.getId() == actual.getId()
                && rol.getId() != seleccionado.getRol().getId()) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol");
        }

        boolean cambiarPassword = password != null && !password.isBlank();
        String passwordFinal = cambiarPassword ? password : seleccionado.getPassword();

        // El constructor de Usuario valida el nombre y la contrasena
        Usuario editado = new Usuario(seleccionado.getId(), username, passwordFinal, rol);

        // Regla: el nombre no puede ser el de otro usuario
        Usuario existente = usuarioDAO.buscarPorUsername(editado.getUsername());
        if (existente != null && existente.getId() != editado.getId()) {
            throw new IllegalArgumentException("Ya existe el usuario " + editado.getUsername());
        }

        if (cambiarPassword) {
            editado.setPassword(Cifrado.sha256(password));   
        }
        usuarioDAO.actualizar(editado, cambiarPassword);
    }

    public void eliminar(Usuario seleccionado, Usuario actual) throws SQLException {
        if (seleccionado == null) {
            throw new IllegalArgumentException("Selecciona un usuario de la tabla");
        }

        // Regla: nadie puede eliminar su propia cuenta mientras la esta usando
        if (actual != null && seleccionado.getId() == actual.getId()) {
            throw new IllegalArgumentException("No puedes eliminar tu propio usuario");
        }

        try {
            usuarioDAO.eliminar(seleccionado.getId());
        } catch (SQLIntegrityConstraintViolationException e) {
            // La base de datos no deja borrar usuarios con datos asociados (por ejemplo, un mesero con pedidos)
            throw new IllegalArgumentException("No se puede eliminar a " + seleccionado.getUsername()
                    + ": tiene registros asociados (por ejemplo, es mesero)");
        }
    }
}