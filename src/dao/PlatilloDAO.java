package dao;
 
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;
import modelo.Platillo;
 

public class PlatilloDAO {
 
    public List<Platillo> listar() throws SQLException {
        List<Platillo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.precio, "
                   + "       c.id AS idCategoria, c.nombre AS nombreCategoria "
                   + "FROM platillos p "
                   + "JOIN categorias c ON p.idCategoria = c.id "
                   + "ORDER BY p.id";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Categoria categoria = new Categoria(rs.getInt("idCategoria"), rs.getString("nombreCategoria"));
                lista.add(new Platillo(rs.getInt("id"), categoria, rs.getString("nombre"), rs.getDouble("precio")));
            }
        }
        return lista;
    }
 
    public void insertar(Platillo platillo) throws SQLException {
        String sql = "INSERT INTO platillos (idCategoria, nombre, precio) VALUES (?, ?, ?)";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, platillo.getCategoria().getId());
            ps.setString(2, platillo.getNombre());
            ps.setDouble(3, platillo.getPrecio());
            ps.executeUpdate();
        }
    }
}

