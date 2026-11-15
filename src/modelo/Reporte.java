package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


  //El resultado de un reporte: un titulo, los nombres de las columnas y las filas de datos.La ventana lo usa para llenar la tabla sin saber de que reporte se trata.

public class Reporte {

    private final String titulo;
    private final List<String> columnas = new ArrayList<>();
    private final List<Object[]> filas = new ArrayList<>();

    public Reporte(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void agregarColumna(String nombre) {
        columnas.add(nombre);
    }

    public void agregarFila(Object[] fila) {
        filas.add(fila);
    }

    public String[] getColumnas() {
        return columnas.toArray(new String[0]);
    }

    public List<Object[]> getFilas() {
        return Collections.unmodifiableList(filas);   // solo lectura desde afuera
    }
}