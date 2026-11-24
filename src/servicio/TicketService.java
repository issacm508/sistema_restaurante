package servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import modelo.DetallePedido;
import modelo.Pedido;

public class TicketService {

    private static final int ANCHO = 36;   

    private final CajaService cajaService = new CajaService();

    public String generar(Pedido pedido, String metodoPago, LocalDateTime fecha) {
        if (pedido == null) {
            throw new IllegalArgumentException("No hay pedido para generar el ticket");
        }
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("Falta el metodo de pago del ticket");
        }

        StringBuilder t = new StringBuilder();
        t.append(centrar("LA COMARCA MEXICANA")).append("\n");
        t.append(centrar("Sabor y tradicion")).append("\n");
        t.append(linea()).append("\n");
        t.append(izqDer("Ticket #" + pedido.getId(),
                fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))).append("\n");
        t.append(izqDer("Mesa: " + pedido.getMesa().getNumeroMesa(),
                "Mesero: " + pedido.getMesero().getNombre())).append("\n");
        t.append(linea()).append("\n");

        for (DetallePedido d : pedido.getDetalles()) {
            String concepto = d.getCantidad() + " x " + d.getPlatillo().getNombre();
            t.append(izqDer(concepto, dinero(d.getSubtotal()))).append("\n");
        }

        t.append(linea()).append("\n");
        t.append(izqDer("Subtotal", dinero(cajaService.calcularSubtotal(pedido)))).append("\n");
        t.append(izqDer("IVA (16%)", dinero(cajaService.calcularIva(pedido)))).append("\n");
        t.append(izqDer("TOTAL", dinero(cajaService.calcularTotal(pedido)))).append("\n");
        t.append(linea()).append("\n");
        t.append("Pago: ").append(metodoPago).append("\n");
        t.append("\n");
        t.append(centrar("Gracias por su visita")).append("\n");
        return t.toString();
    }

    private String dinero(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    private String linea() {
        return "-".repeat(ANCHO);
    }

    private String centrar(String texto) {
        int espacios = Math.max(0, (ANCHO - texto.length()) / 2);
        return " ".repeat(espacios) + texto;
    }

    private String izqDer(String izq, String der) {
        int maxIzq = ANCHO - der.length() - 1;
        if (izq.length() > maxIzq) {
            izq = izq.substring(0, Math.max(0, maxIzq));
        }
        return izq + " ".repeat(ANCHO - izq.length() - der.length()) + der;
    }
}