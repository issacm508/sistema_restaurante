package servicio;

import dao.MesaDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.Mesa;


public class MesaService {

    // Los mismos textos que se guardan en la columna estado de la tabla mesas
    public static final String DISPONIBLE = "Disponible";
    public static final String OCUPADA = "Ocupada";

    // El numero de mesa mas alto que se permite
    public static final int NUMERO_MAXIMO = 999;

    private final MesaDAO mesaDAO = new MesaDAO();

    public List<Mesa> obtenerTodas() throws SQLException {
        return mesaDAO.listar();
    }

    public void agregar(String numeroTexto) throws SQLException {
        String texto = (numeroTexto == null) ? "" : numeroTexto.trim();

        if (!texto.matches("-?\\d+")) {
            throw new IllegalArgumentException("El numero de mesa debe ser un numero entero");
        }

        // Es un entero valido, pero puede ser demasiado grande para guardarlo (por ejemplo 1234556631344)
        int numero;
        try {
            numero = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeNumeroGrande());
        }
        if (numero > NUMERO_MAXIMO) {
            throw new IllegalArgumentException(mensajeNumeroGrande());
        }

        // El constructor de Mesa valida que el numero sea mayor a 0
        Mesa nueva = new Mesa(0, numero, DISPONIBLE);

        // Regla de negocio: no puede haber dos mesas con el mismo numero
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
        // Regla: la mesa no se libera a mano mientras su pedido no se haya cobrado
        if (mesaDAO.tienePedidoSinCobrar(mesa.getId())) {
            throw new IllegalArgumentException("La mesa " + mesa.getNumeroMesa()
                    + " tiene un pedido sin cobrar; se libera cuando se cobre en Caja");
        }
        mesaDAO.actualizarEstado(mesa.getId(), DISPONIBLE);
    }

    private String mensajeNumeroGrande() {
        return "El numero de mesa no puede ser mayor a " + NUMERO_MAXIMO;
    }
}