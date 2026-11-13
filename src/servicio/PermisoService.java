package servicio;
 
import modelo.Roles;
import modelo.Usuario;
 
public class PermisoService {
 
    private int idRol(Usuario usuario) {
        return usuario.getRol().getId();
    }
 
    private boolean esSuperAdministrador(Usuario usuario) {
        return idRol(usuario) == Roles.SUPER_ADMINISTRADOR;
    }
 
    public boolean puedeAdministrar(Usuario usuario) {
        return esSuperAdministrador(usuario);
    }
 
    public boolean puedeVerMesas(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.RECEPCIONISTA;
    }
 
    public boolean puedeVerPedidos(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.MESERO;
    }
 
    public boolean puedeVerCocina(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.COCINA;
    }
 
    public boolean puedeCobrar(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.ADMINISTRADOR;
    }
}
