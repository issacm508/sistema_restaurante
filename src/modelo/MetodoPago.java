package modelo;

public class MetodoPago {

    private int id;
    private String nombre;

    public MetodoPago() {
    }

    public MetodoPago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Lo usa el JComboBox de la pantalla de Caja para mostrar el nombre
    @Override
    public String toString() {
        return nombre;
    }
}
