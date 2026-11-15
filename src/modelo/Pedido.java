package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {

    public static final String EN_PROCESO = "En Proceso";

    private int id;
    private Mesa mesa;       // objetos, no solo los ids (asi lo pide el diagrama de clases)
    private Mesero mesero;
    private LocalDateTime fechaHora;
    private String estado;
    private final List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Mesa mesa, Mesero mesero) {
        setMesa(mesa);
        setMesero(mesero);
        this.fechaHora = LocalDateTime.now();
        this.estado = EN_PROCESO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("El pedido necesita una mesa");
        }
        this.mesa = mesa;
    }

    public Mesero getMesero() {
        return mesero;
    }

    public void setMesero(Mesero mesero) {
        if (mesero == null) {
            throw new IllegalArgumentException("El pedido necesita un mesero");
        }
        this.mesero = mesero;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);   // solo lectura desde afuera
    }

    // Metodo del diagrama UML: calcularTotal()
    public double calcularTotal() {
        double total = 0;
        for (DetallePedido d : detalles) {
            total += d.getSubtotal();
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
