package modelo;
 
public class Mesa {
 
    private int id;
    private int numeroMesa;
    private String estado;   
 
    public Mesa() {
    }
 
    public Mesa(int id, int numeroMesa, String estado) {
        this.id = id;
        setNumeroMesa(numeroMesa);
        this.estado = estado;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public int getNumeroMesa() {
        return numeroMesa;
    }
 
    public void setNumeroMesa(int numeroMesa) {
        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("El numero de mesa debe ser mayor a 0");
        }
        this.numeroMesa = numeroMesa;
    }
 
    public String getEstado() {
        return estado;
    }
 
    public void setEstado(String estado) {
        this.estado = estado;
    }
 
    public void actualizarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
 
    @Override
    public String toString() {
        return "Mesa " + numeroMesa + " (" + estado + ")";
    }
}