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

    // Usuarios y roles, catalogo del menu (platillos) y reportes
    public boolean puedeAdministrar(Usuario usuario) {
        return esSuperAdministrador(usuario);
    }

    // Asignar mesa libre
    public boolean puedeVerMesas(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.RECEPCIONISTA;
    }

    // Registrar, modificar o cancelar pedidos
    public boolean puedeVerPedidos(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.MESERO;
    }

    // Actualizar el estado de los pedidos (En proceso / Listo)
    public boolean puedeVerCocina(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.COCINA;
    }

    // Procesar pagos y generar ticket
    public boolean puedeCobrar(Usuario usuario) {
        return esSuperAdministrador(usuario) || idRol(usuario) == Roles.ADMINISTRADOR;
    }
}