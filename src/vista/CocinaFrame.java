package vista;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Pedido;
import servicio.PedidoService;

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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  
        setSize(920, 420);
        setLocationRelativeTo(null);

        // --- tabla (centro) ---
        modeloTabla = new DefaultTableModel(new String[]{"Pedido", "Mesa", "Mesero", "Hora", "Platillos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla) {
            // Si el tema cambia la altura de las filas, se vuelven a calcular segun los platillos de cada pedido
            @Override
            public void setRowHeight(int alto) {
                super.setRowHeight(alto);
                ajustarAlturas(this);
            }
        };
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);   
        tabla.getColumnModel().getColumn(4).setPreferredWidth(400);   

        tabla.getColumnModel().getColumn(4).setCellRenderer(crearRenderizadorPlatillos());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- botones (abajo) ---
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel("Se actualiza cada " + SEGUNDOS_ACTUALIZACION + " segundos"));
        JButton btnActualizar = new JButton("Actualizar ahora");
        JButton btnListo = new JButton("Marcar como listo");
        JButton btnCancelar = new JButton("Cancelar pedido");
        panel.add(btnActualizar);
        panel.add(btnListo);
        panel.add(btnCancelar);
        add(panel, BorderLayout.SOUTH);

        // --- eventos ---
        btnActualizar.addActionListener(e -> refrescarTabla());
        btnListo.addActionListener(e -> marcarListo());
        btnCancelar.addActionListener(e -> cancelarPedido());

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
            return null;   
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

    private void cancelarPedido() {
        Pedido seleccionado = pedidoSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Cancelar el pedido #" + seleccionado.getId() + " de la mesa "
                        + seleccionado.getMesa().getNumeroMesa() + "?\nLa mesa quedara disponible.",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicio.cancelar(seleccionado);
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

            ajustarAlturas(tabla);

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

    private void ajustarAlturas(JTable t) {
        if (modeloTabla == null || t.getColumnCount() < 5) {
            return;  
        }
        int alturaLinea = t.getFontMetrics(Tema.FUENTE).getHeight();
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            Object texto = modeloTabla.getValueAt(fila, 4);
            int lineas = (texto == null) ? 1 : texto.toString().split("\\n").length;
            int alto = Math.max(t.getRowHeight(), lineas * alturaLinea + 10);
            if (t.getRowHeight(fila) != alto) {
                t.setRowHeight(fila, alto);
            }
        }
    }

    private TableCellRenderer crearRenderizadorPlatillos() {
        JTextArea area = new JTextArea();
        area.setFont(Tema.FUENTE);
        area.setOpaque(true);
        area.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 4, 3, 4));
        return (JTable t, Object valor, boolean seleccionada, boolean foco, int fila, int columna) -> {
            area.setText(valor == null ? "" : valor.toString());
            area.setBackground(seleccionada ? t.getSelectionBackground()
                    : (fila % 2 == 0 ? Tema.FILA_BASE : Tema.SUPERFICIE));
            area.setForeground(seleccionada ? t.getSelectionForeground() : Tema.TEXTO);
            return (Component) area;
        };
    }

    private String resumenPlatillos(Pedido pedido) {
        StringBuilder texto = new StringBuilder();
        for (DetallePedido d : pedido.getDetalles()) {
            if (texto.length() > 0) {
                texto.append("\n");
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
