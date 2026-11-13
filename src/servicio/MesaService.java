package servicio;
 
import dao.MesaDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.Mesa;
 
public class MesaService {
 
    public static final String DISPONIBLE = "Disponible";
    public static final String OCUPADA = "Ocupada";
 
    private final MesaDAO mesaDAO = new MesaDAO();
 
    public List<Mesa> obtenerTodas() throws SQLException {
        return mesaDAO.listar();
    }
 
    public void agregar(String numeroTexto) throws SQLException {
        int numero;
        try {
            numero = Integer.parseInt(numeroTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El numero de mesa debe ser un numero entero");
        }
 
        Mesa nueva = new Mesa(0, numero, DISPONIBLE);
 
        for (Mesa existente : mesaDAO.listar()) {
            if (existente.getNumeroMesa() == numero) {
                throw new IllegalArgumentException("Ya existe la mesa " + numero);
            }
        }
 
        mesaDAO.insertar(nueva);
    }
 
    public void ocupar(Mesa mesa) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("Selecciona una mesa de la tabla");
        }
        if (OCUPADA.equals(mesa.getEstado())) {
            throw new IllegalArgumentException("La mesa " + mesa.getNumeroMesa() + " ya esta ocupada");
        }
        mesaDAO.actualizarEstado(mesa.getId(), OCUPADA);
    }
 
    public void liberar(Mesa mesa) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("Selecciona una mesa de la tabla");
        }
        if (DISPONIBLE.equals(mesa.getEstado())) {
            throw new IllegalArgumentException("La mesa " + mesa.getNumeroMesa() + " ya esta disponible");
        }
        mesaDAO.actualizarEstado(mesa.getId(), DISPONIBLE);
    }
}
