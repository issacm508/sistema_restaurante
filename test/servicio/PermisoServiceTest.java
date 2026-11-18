package servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelo.Rol;
import modelo.Roles;
import modelo.Usuario;
import org.junit.jupiter.api.Test;


 // Pruebas unitarias de PermisoService: que modulo puede usar cada rol.No usan la base de datos.

public class PermisoServiceTest {

    private final PermisoService permisos = new PermisoService();

    private Usuario conRol(int idRol, String nombreRol) {
        return new Usuario(1, "prueba", "123", new Rol(idRol, nombreRol));
    }

    private final Usuario superAdmin = conRol(Roles.SUPER_ADMINISTRADOR, "Super Administrador");
    private final Usuario recepcionista = conRol(Roles.RECEPCIONISTA, "Recepcionista");
    private final Usuario mesero = conRol(Roles.MESERO, "Mesero");
    private final Usuario cocina = conRol(Roles.COCINA, "Cocina");
    private final Usuario administrador = conRol(Roles.ADMINISTRADOR, "Administrador");

    @Test
    void elSuperAdministradorTieneAccesoATodo() {
        assertTrue(permisos.puedeAdministrar(superAdmin));
        assertTrue(permisos.puedeVerMesas(superAdmin));
        assertTrue(permisos.puedeVerPedidos(superAdmin));
        assertTrue(permisos.puedeVerCocina(superAdmin));
        assertTrue(permisos.puedeCobrar(superAdmin));
    }

    @Test
    void soloElSuperAdministradorPuedeAdministrar() {
        assertTrue(permisos.puedeAdministrar(superAdmin));
        assertFalse(permisos.puedeAdministrar(recepcionista));
        assertFalse(permisos.puedeAdministrar(mesero));
        assertFalse(permisos.puedeAdministrar(cocina));
        assertFalse(permisos.puedeAdministrar(administrador));
    }

    @Test
    void mesasLasVenSuperYRecepcionista() {
        assertTrue(permisos.puedeVerMesas(superAdmin));
        assertTrue(permisos.puedeVerMesas(recepcionista));
        assertFalse(permisos.puedeVerMesas(mesero));
        assertFalse(permisos.puedeVerMesas(cocina));
        assertFalse(permisos.puedeVerMesas(administrador));
    }

    @Test
    void pedidosLosVenSuperYMesero() {
        assertTrue(permisos.puedeVerPedidos(superAdmin));
        assertTrue(permisos.puedeVerPedidos(mesero));
        assertFalse(permisos.puedeVerPedidos(recepcionista));
        assertFalse(permisos.puedeVerPedidos(cocina));
        assertFalse(permisos.puedeVerPedidos(administrador));
    }

    @Test
    void cocinaLaVenSuperYCocina() {
        assertTrue(permisos.puedeVerCocina(superAdmin));
        assertTrue(permisos.puedeVerCocina(cocina));
        assertFalse(permisos.puedeVerCocina(recepcionista));
        assertFalse(permisos.puedeVerCocina(mesero));
        assertFalse(permisos.puedeVerCocina(administrador));
    }

    @Test
    void cobrarLoPuedenSuperYAdministrador() {
        assertTrue(permisos.puedeCobrar(superAdmin));
        assertTrue(permisos.puedeCobrar(administrador));
        assertFalse(permisos.puedeCobrar(recepcionista));
        assertFalse(permisos.puedeCobrar(mesero));
        assertFalse(permisos.puedeCobrar(cocina));
    }
}