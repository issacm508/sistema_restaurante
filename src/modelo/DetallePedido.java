package modelo;

// Una linea de un pedido: un platillo y cuantas unidades se pidieron.

public class DetallePedido {

    private int id;
    private Platillo platillo;   // objeto Platillo, no solo el id
    private int cantidad;

    public DetallePedido() {
    }

    public DetallePedido(Platillo platillo, int cantidad) {
        setPlatillo(platillo);   // se usan los setters para que se apliquen las validaciones
        setCantidad(cantidad);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Platillo getPlatillo() {
        return platillo;
    }

    public void setPlatillo(Platillo platillo) {
        if (platillo == null) {
            throw new IllegalArgumentException("Selecciona un platillo");
        }
        this.platillo = platillo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad;
    }

    // El subtotal se CALCULA, no se guarda como atributo: asi nunca queda desactualizado.
    public double getSubtotal() {
        return Math.round(platillo.getPrecio() * cantidad * 100.0) / 100.0;
    }
}
