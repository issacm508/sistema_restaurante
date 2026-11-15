package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.MetodoPago;
import modelo.Pedido;
import servicio.CajaService;


//Capa de vista: pantalla de Caja. Muestra los pedidos Completados quetodavia no se han cobrado y permite cobrarlos con un metodo de pago.

public class CajaFrame extends JFrame {

    private final CajaService servicio = new CajaService();
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    // pedidos que se muestran en la tabla (misma posicion que las filas)
    private List<Pedido> pedidos = new ArrayList<>();

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextArea txtDetalle;
    private JComboBox<MetodoPago> cmbMetodo;
    private JLabel lblTotal;

    public CajaFrame() {
        setTitle("Caja");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // cierra solo esta ventana, no toda la app
        setLayout(new BorderLayout(10, 10));

        construirTabla();
        construirPanelInferior();
        cargarMetodosPago();
        cargarPedidos();
    }

    private void construirTabla() {
        modeloTabla = new DefaultTableModel(
                new String[]{"Pedido", "Mesa", "Mesero", "Hora", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;   // la tabla es solo de lectura
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(800, 230));
        add(scrollTabla, BorderLayout.CENTER);
    }

    private void construirPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        // detalle del pedido seleccionado
        txtDetalle = new JTextArea(6, 40);
        txtDetalle.setEditable(false);
        panelInferior.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);

        // metodo de pago, total y botones
        JPanel panelCobro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        lblTotal = new JLabel("Total: $0.00");
        cmbMetodo = new JComboBox<>();

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarPedidos());

        JButton btnCobrar = new JButton("Cobrar");
        btnCobrar.addActionListener(e -> cobrar());

        panelCobro.add(lblTotal);
        panelCobro.add(new JLabel("Metodo de pago:"));
        panelCobro.add(cmbMetodo);
        panelCobro.add(btnActualizar);
        panelCobro.add(btnCobrar);

        panelInferior.add(panelCobro, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarMetodosPago() {
        try {
            for (MetodoPago metodo : servicio.listarMetodosPago()) {
                cmbMetodo.addItem(metodo);
            }
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar los metodos de pago: " + ex.getMessage());
        }
    }

    private void cargarPedidos() {
        try {
            pedidos = servicio.listarPorCobrar();
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar los pedidos: " + ex.getMessage());
            return;
        }

        modeloTabla.setRowCount(0);   // limpia la tabla

        for (Pedido p : pedidos) {
            String mesero = p.getMesero().getNombre() + " " + p.getMesero().getApellido();
            String hora = (p.getFechaHora() != null) ? p.getFechaHora().format(formatoHora) : "";

            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getMesa().getNumeroMesa(),
                mesero,
                hora,
                String.format("$%.2f", servicio.calcularTotal(p))
            });
        }
        mostrarDetalle();
    }

    // Muestra los platillos y el total del pedido seleccionado
    private void mostrarDetalle() {
        Pedido pedido = pedidoSeleccionado();

        if (pedido == null) {
            txtDetalle.setText("");
            lblTotal.setText("Total: $0.00");
            return;
        }

        StringBuilder texto = new StringBuilder();
        for (DetallePedido d : pedido.getDetalles()) {
            texto.append(d.getCantidad()).append(" x ")
                 .append(d.getPlatillo().getNombre())
                 .append("   $").append(String.format("%.2f", d.getSubtotal()))
                 .append("\n");
        }
        txtDetalle.setText(texto.toString());
        lblTotal.setText(String.format("Total: $%.2f", servicio.calcularTotal(pedido)));
    }

    private Pedido pedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= pedidos.size()) {
            return null;
        }
        return pedidos.get(fila);
    }

    private void cobrar() {
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla",
                    "Caja", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MetodoPago metodo = (MetodoPago) cmbMetodo.getSelectedItem();
        double total = servicio.calcularTotal(pedido);

        int respuesta = JOptionPane.showConfirmDialog(this,
                String.format("Cobrar $%.2f del pedido #%d (mesa %d) con %s?",
                        total, pedido.getId(), pedido.getMesa().getNumeroMesa(), metodo),
                "Confirmar cobro", JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            servicio.cobrar(pedido, metodo);
            JOptionPane.showMessageDialog(this, "Cobro registrado. La mesa quedo disponible.",
                    "Caja", JOptionPane.INFORMATION_MESSAGE);
            cargarPedidos();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Caja", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo registrar el cobro: " + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}