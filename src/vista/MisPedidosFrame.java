package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Usuario;
import servicio.PedidoService;

public class MisPedidosFrame extends JFrame {

    private final PedidoService servicio = new PedidoService();
    private final Usuario usuario;

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    // Los pedidos que se muestran, en el mismo orden que las filas de la tabla
    private List<Pedido> pedidos = new ArrayList<>();

    public MisPedidosFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Mis pedidos - En proceso");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(860, 440);
        setLocationRelativeTo(null);

        // --- tabla (centro) ---
        modeloTabla = new DefaultTableModel(new String[]{"Pedido", "Mesa", "Mesero", "Hora", "Platillos"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla) {
            // Si el tema cambia la altura de las filas, se vuelven a calcular segun los platillos
            @Override
            public void setRowHeight(int alto) {
                super.setRowHeight(alto);
                ajustarAlturas(this);
            }
        };
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(350);
        tabla.getColumnModel().getColumn(4).setCellRenderer(crearRenderizadorPlatillos());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- botones (abajo) ---
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel("Si la cocina ya lo marco listo, ya no se puede cancelar"));
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnCancelar = new JButton("Cancelar pedido");
        panel.add(btnActualizar);
        panel.add(btnCancelar);
        add(panel, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> refrescarTabla());
        btnCancelar.addActionListener(e -> cancelarPedido());

        refrescarTabla();
    }

    private Pedido pedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= pedidos.size()) {
            return null;
        }
        return pedidos.get(fila);
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
                        + seleccionado.getMesa().getNumeroMesa() + "?\n"
                        + "La mesa sigue ocupada hasta que la recepcionista la libere.",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicio.cancelar(usuario, seleccionado);
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void refrescarTabla() {
        try {
            pedidos = servicio.obtenerPedidosCancelables(usuario);
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
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    // Cada fila crece segun cuantos platillos (lineas) tenga el pedido
    private void ajustarAlturas(JTable t) {
        if (modeloTabla == null || t.getColumnCount() < 5) {
            return;   // todavia se esta construyendo la tabla
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

    // Celda de varias lineas (un platillo por linea) con los colores de la tabla
    private TableCellRenderer crearRenderizadorPlatillos() {
        JTextArea area = new JTextArea();
        area.setFont(Tema.FUENTE);
        area.setOpaque(true);
        area.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        return (JTable t, Object valor, boolean seleccionada, boolean foco, int fila, int columna) -> {
            area.setText(valor == null ? "" : valor.toString());
            area.setBackground(seleccionada ? t.getSelectionBackground()
                    : (fila % 2 == 0 ? Tema.FILA_BASE : Tema.SUPERFICIE));
            area.setForeground(seleccionada ? t.getSelectionForeground() : Tema.TEXTO);
            return (Component) area;
        };
    }

    // "2 x Milanesa de Res", un platillo por linea
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