package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelo.Mesa;
import org.junit.jupiter.api.Test;


 
//Pruebas unitarias de MesaService: las reglas que fallan antes de consultar la base de datos.
 
public class MesaServiceTest {

    private final MesaService servicio = new MesaService();

    @Test
    void agregarRechazaTextoQueNoEsNumero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("abc"));

        assertEquals("El numero de mesa debe ser un numero entero", ex.getMessage());
    }

    @Test
    void agregarRechazaUnTextoNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar(null));

        assertEquals("El numero de mesa debe ser un numero entero", ex.getMessage());
    }

    @Test
    void agregarRechazaUnNumeroDecimal() {
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("2.5"));
    }

    @Test
    void agregarRechazaElCero() {
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("0"));
    }

    @Test
    void agregarRechazaUnNumeroNegativo() {
        assertThrows(IllegalArgumentException.class, () -> servicio.agregar("-4"));
    }

    @Test
    void agregarRechazaUnEnteroMasGrandeQueElMaximo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("1000"));

        assertEquals("El numero de mesa no puede ser mayor a 999", ex.getMessage());
    }

    @Test
    void agregarRechazaUnEnteroEnormeConUnMensajeCorrecto() {
        // Es un numero entero valido, pero no cabe en un int: el mensaje no debe decir que no es entero
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.agregar("1234556631344"));

        assertEquals("El numero de mesa no puede ser mayor a 999", ex.getMessage());
    }

    @Test
    void ocuparRechazaUnaMesaNula() {
        assertThrows(IllegalArgumentException.class, () -> servicio.ocupar(null));
    }

    @Test
    void ocuparRechazaUnaMesaQueYaEstaOcupada() {
        Mesa ocupada = new Mesa(1, 3, MesaService.OCUPADA);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.ocupar(ocupada));

        assertEquals("La mesa 3 ya esta ocupada", ex.getMessage());
    }

    @Test
    void liberarRechazaUnaMesaNula() {
        assertThrows(IllegalArgumentException.class, () -> servicio.liberar(null));
    }

    @Test
    void liberarRechazaUnaMesaQueYaEstaDisponible() {
        Mesa libre = new Mesa(1, 3, MesaService.DISPONIBLE);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.liberar(libre));

        assertEquals("La mesa 3 ya esta disponible", ex.getMessage());
    }
}