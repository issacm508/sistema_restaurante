package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

private static final String URL =
    "jdbc:mysql://localhost:3306/sistema_restaurante?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USUARIO = "restaurante";
private static final String PASSWORD = "restaurante123$";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
