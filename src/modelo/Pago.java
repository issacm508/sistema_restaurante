package modelo;

import java.time.LocalDateTime;

public class Pago {

    private int id;
    private int idPedido;
    private double monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;

    public Pago() {
    }

    public Pago(int idPedido, double monto, MetodoPago metodoPago) {
        this.idPedido = idPedido;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}
