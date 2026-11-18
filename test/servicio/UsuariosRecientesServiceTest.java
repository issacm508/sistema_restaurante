package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas unitarias de UsuariosRecientesService. Usan un archivo temporal, no tocan la base de datos.
 */
public class UsuariosRecientesServiceTest {

    @TempDir
    Path carpeta;

    private UsuariosRecientesService nuevoServicio() {
        return new UsuariosRecientesService(carpeta.resolve("usuarios.txt"));
    }

    @Test
    void sinArchivoNoHayUsuariosRecordados() {
        assertTrue(nuevoServicio().obtener().isEmpty());
    }

    @Test
    void elUltimoUsuarioQuedaPrimeroYNoSeRepite() {
        UsuariosRecientesService servicio = nuevoServicio();
        servicio.recordar("Maria Lopez");
        servicio.recordar("Carlos Vallarta");
        servicio.recordar("Maria Lopez");

        List<String> nombres = servicio.obtener();

        assertEquals(2, nombres.size());
        assertEquals("Maria Lopez", nombres.get(0));
        assertEquals("Carlos Vallarta", nombres.get(1));
    }

    @Test
    void seIgnoranLosNombresVaciosYSoloSeGuardaElMaximo() {
        UsuariosRecientesService servicio = nuevoServicio();
        servicio.recordar("   ");
        servicio.recordar(null);
        for (int i = 1; i <= UsuariosRecientesService.MAXIMO + 3; i++) {
            servicio.recordar("usuario" + i);
        }

        List<String> nombres = servicio.obtener();

        assertEquals(UsuariosRecientesService.MAXIMO, nombres.size());
        assertEquals("usuario" + (UsuariosRecientesService.MAXIMO + 3), nombres.get(0));
    }
}