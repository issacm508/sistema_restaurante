package modelo;

public class Mesero {

    private int id;
    private int idUsuario;   
    private String nombre;
    private String apellido;
    private String telefono;   
    private boolean activo;

    public Mesero() {
    }

    public Mesero(int id, int idUsuario, String nombre, String apellido, boolean activo) {
        this(id, idUsuario, nombre, apellido, null, activo);
    }

    public Mesero(int id, int idUsuario, String nombre, String apellido, String telefono, boolean activo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            this.telefono = null;
            return;
        }
        String limpio = telefono.trim();
        if (!limpio.matches("\\d{10}")) {
            throw new IllegalArgumentException("El telefono debe tener 10 digitos");
        }
        this.telefono = limpio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}



