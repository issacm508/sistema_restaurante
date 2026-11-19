package servicio;

import dao.CategoriaDAO;
import dao.PlatilloDAO;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import modelo.Categoria;
import modelo.Platillo;

public class PlatilloService {

    // El precio mas alto que se permite
    public static final double PRECIO_MAXIMO = 9999.99;

    private final PlatilloDAO platilloDAO = new PlatilloDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Platillo> obtenerTodos() throws SQLException {
        return platilloDAO.listar();
    }

    public List<Categoria> obtenerCategorias() throws SQLException {
        return categoriaDAO.listar();
    }

    public void agregar(String nombre, String precioTexto, Categoria categoria) throws SQLException {
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoria");
        }
        double precio = convertirPrecio(precioTexto);

        // El constructor de Platillo valida que el nombre no este vacio y que el precio sea mayor a 0
        Platillo nuevo = new Platillo(0, categoria, nombre, precio);
        platilloDAO.insertar(nuevo);
    }

    // Cambia el nombre, el precio y la categoria del platillo seleccionado en la tabla. 
    public void actualizar(Platillo seleccionado, String nombre, String precioTexto, Categoria categoria)
            throws SQLException {
        if (seleccionado == null) {
            throw new IllegalArgumentException("Selecciona un platillo de la tabla");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoria");
        }
        double precio = convertirPrecio(precioTexto);

        Platillo cambiado = new Platillo(seleccionado.getId(), categoria, nombre, precio);
        platilloDAO.actualizar(cambiado);
    }

    //Elimina el platillo seleccionado. No se puede si ya aparece en algun pedido. 
    public void eliminar(Platillo seleccionado) throws SQLException {
        if (seleccionado == null) {
            throw new IllegalArgumentException("Selecciona un platillo de la tabla");
        }
        try {
            platilloDAO.eliminar(seleccionado.getId());
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("No se puede eliminar " + seleccionado.getNombre()
                    + ": ya aparece en pedidos registrados");
        }
    }

    // Convierte el texto del precio a numero y revisa que tenga sentido
    private double convertirPrecio(String precioTexto) {
        String texto = (precioTexto == null) ? "" : precioTexto.trim();
        double precio;
        try {
            precio = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un numero");
        }
        // "NaN" e "Infinity" tambien se pueden convertir, pero no son precios
        if (Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio debe ser un numero");
        }
        if (precio > PRECIO_MAXIMO) {
            throw new IllegalArgumentException("El precio no puede ser mayor a " + PRECIO_MAXIMO);
        }
        return precio;
    }
}
