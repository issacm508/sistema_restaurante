package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Pedido;
import servicio.PedidoService;

//Capa de vista: pantalla de la cocina. Muestra los pedidos En Proceso y permite marcarlos como listos. Se actualiza sola cada pocos segundos, asi la cocina ve los pedidos nuevos sin hacer nada.
public class CocinaFrame extends JFrame {

    private static final int SEGUNDOS_ACTUALIZACION = 10;

    private final PedidoService servicio = new PedidoService();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final Timer temporizador;

    // Los pedidos que se estan mostrando, en el mismo orden que las filas de la tabla
    private List<Pedido> pedidos = new ArrayList<>();

    public CocinaFrame() {
        setTitle("Cocina - Pedidos en proceso");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(920, 420);
        setLocationRelativeTo(null);

        // --- tabla (centro) ---
        modeloTabla = new DefaultTableModel(new String[]{"Pedido", "Mesa", "Mesero", "Hora", "Platillos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(400);   // la columna de platillos es la mas ancha
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- botones (abajo) ---
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel("Se actualiza cada " + SEGUNDOS_ACTUALIZACION + " segundos"));
        JButton btnActualizar = new JButton("Actualizar ahora");
        JButton btnListo = new JButton("Marcar como listo");
        panel.add(btnActualizar);
        panel.add(btnListo);
        add(panel, BorderLayout.SOUTH);

        // --- eventos ---
        btnActualizar.addActionListener(e -> refrescarTabla());
        btnListo.addActionListener(e -> marcarListo());

        // Temporizador de Swing: llama a refrescarTabla cada cierto tiempo
        temporizador = new Timer(SEGUNDOS_ACTUALIZACION * 1000, e -> refrescarTabla());
        temporizador.start();

        // Al cerrar la ventana se detiene el temporizador
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                temporizador.stop();
            }
        });

        refrescarTabla();
    }

    private Pedido pedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= pedidos.size()) {
            return null;   // no hay ninguna fila seleccionada
        }
        return pedidos.get(fila);
    }

    private void marcarListo() {
        try {
            servicio.marcarComoCompletado(pedidoSeleccionado());
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void refrescarTabla() {
        // Se recuerda el pedido seleccionado para no perder la seleccion cuando la tabla se actualiza sola
        Pedido seleccionado = pedidoSeleccionado();

        try {
            pedidos = servicio.obtenerPedidosEnProceso();
            modeloTabla.setRowCount(0);

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            for (Pedido p : pedidos) {
                String hora = (p.getFechaHora() == null) ? "" : p.getFechaHora().format(formato);
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getMesa().getNumeroMesa(), p.getMesero().getNombreCompleto(),
                    hora, resumenPlatillos(p)
                });
            }

            if (seleccionado != null) {
                for (int i = 0; i < pedidos.size(); i++) {
                    if (pedidos.get(i).getId() == seleccionado.getId()) {
                        tabla.setRowSelectionInterval(i, i);
                    }
                }
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    // Convierte los detalles del pedido en un texto como "2 x Milanesa de Res, 1 x Flan Napolitano"
    private String resumenPlatillos(Pedido pedido) {
        StringBuilder texto = new StringBuilder();
        for (DetallePedido d : pedido.getDetalles()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(d.getCantidad()).append(" x ").append(d.getPlatillo().getNombre());
        }
        return texto.toString();
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}