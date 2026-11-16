package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


//Pruebas unitarias de Mesa.

public class MesaTest {

    @Test
    void creaUnaMesaValida() {
        Mesa mesa = new Mesa(1, 5, "Disponible");

        assertEquals(5, mesa.getNumeroMesa());
        assertEquals("Disponible", mesa.getEstado());
    }

    @Test
    void rechazaUnNumeroDeMesaDeCero() {
        assertThrows(IllegalArgumentException.class, () -> new Mesa(1, 0, "Disponible"));
    }

    @Test
    void rechazaUnNumeroDeMesaNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Mesa(1, -3, "Disponible"));
    }

    @Test
    void actualizarEstadoCambiaElEstado() {
        Mesa mesa = new Mesa(1, 5, "Disponible");

        mesa.actualizarEstado("Ocupada");

        assertEquals("Ocupada", mesa.getEstado());
    }
}
