package servicio;
 
import dao.CategoriaDAO;
import dao.PlatilloDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.Categoria;
import modelo.Platillo;
 
public class PlatilloService {
 
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
 
        double precio;
        try {
            precio = Double.parseDouble(precioTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un numero");
        }
 
        Platillo nuevo = new Platillo(0, categoria, nombre, precio);
        platilloDAO.insertar(nuevo);
    }
}
