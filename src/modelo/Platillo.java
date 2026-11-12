package modelo;
 
public class Platillo {
 
    private int id;
    private Categoria categoria;   
    private String nombre;
    private double precio;
 
    public Platillo() {
    }
 
    public Platillo(int id, Categoria categoria, String nombre, double precio) {
        this.id = id;
        this.categoria = categoria;
        setNombre(nombre);   
        setPrecio(precio);
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public Categoria getCategoria() {
        return categoria;
    }
 
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del platillo no puede estar vacio");
        }
        this.nombre = nombre.trim();
    }
 
    public double getPrecio() {
        return precio;
    }
 
    public void setPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        this.precio = precio;
    }
 
    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}