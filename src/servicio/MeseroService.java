package servicio;

import dao.MeseroDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.Mesero;

public class MeseroService {

    private final MeseroDAO meseroDAO = new MeseroDAO();

    public List<Mesero> obtenerTodos() throws SQLException {
        return meseroDAO.listar();
    }

    public void actualizar(Mesero seleccionado, String nombre, String apellido,
                           String telefono, boolean activo) throws SQLException {
        if (seleccionado == null) {
            throw new IllegalArgumentException("Selecciona un mesero de la tabla");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar vacio");
        }

        // Se prepara una copia: si el telefono es invalido, el mesero de la tabla no se modifica
        Mesero cambios = new Mesero(seleccionado.getId(), seleccionado.getIdUsuario(),
                nombre.trim(), apellido.trim(), activo);
        cambios.setTelefono(telefono);   // lanza el aviso si no tiene 10 digitos

        meseroDAO.actualizar(cambios);
    }
}