package servicio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de servicio: recuerda los nombres de usuario que ya iniciaron sesion en esta computadora,
 * para no tener que escribirlos cada vez. NUNCA guarda contrasenas.
 * El archivo queda en la carpeta personal del usuario de Windows, no en el proyecto (asi no se sube a GitHub).
 */
public class UsuariosRecientesService {

    // Cuantos nombres se recuerdan como maximo
    public static final int MAXIMO = 8;

    private final Path archivo;

    public UsuariosRecientesService() {
        this(Paths.get(System.getProperty("user.home"), ".la_comarca_usuarios.txt"));
    }

    // Constructor para las pruebas: permite usar un archivo temporal
    public UsuariosRecientesService(Path archivo) {
        this.archivo = archivo;
    }

    /** Los nombres recordados, el mas reciente primero. Si algo falla devuelve una lista vacia. */
    public List<String> obtener() {
        List<String> nombres = new ArrayList<>();
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.trim().isEmpty()) {
                        nombres.add(linea.trim());
                    }
                }
            }
        } catch (IOException e) {
            // no es grave: simplemente no hay usuarios recordados
        }
        return nombres;
    }

    /** Guarda un nombre al principio de la lista (sin repetirlo) y recorta a MAXIMO. */
    public void recordar(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String nombre = username.trim();

        List<String> nombres = obtener();
        nombres.remove(nombre);
        nombres.add(0, nombre);
        while (nombres.size() > MAXIMO) {
            nombres.remove(nombres.size() - 1);
        }

        try {
            Files.write(archivo, nombres, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // no es grave: el login funciona igual, solo no se recuerda el nombre
        }
    }
}
