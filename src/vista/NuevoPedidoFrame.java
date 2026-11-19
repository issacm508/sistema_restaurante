package vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Pedido;
import modelo.Platillo;
import modelo.Usuario;
import servicio.PedidoService;
import servicio.PlatilloService;

public class NuevoPedidoFrame extends JFrame {

    private final Usuario usuario;   // el usuario que inicio sesion (el mesero)
    private final PedidoService pedidoService = new PedidoService();
    private final PlatilloService platilloService = new PlatilloService();

    private final JComboBox<Mesa> cmbMesa = new JComboBox<>();
    private final JComboBox<Platillo> cmbPlatillo = new JComboBox<>();
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JLabel lblTotal = new JLabel("Total: $0.00");

    // Las lineas del pedido que se esta armando (todavia no se guardan en la base de datos)
    private final List<DetallePedido> detalles = new ArrayList<>();

    public NuevoPedidoFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Nuevo pedido");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(780, 480);
        setLocationRelativeTo(null);

        // --- parte de arriba: mesa, platillo y cantidad ---
        JPanel panelArriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelArriba.add(new JLabel("Mesa:"));
        panelArriba.add(cmbMesa);
        panelArriba.add(new JLabel("Platillo:"));
        panelArriba.add(cmbPlatillo);
        panelArriba.add(new JLabel("Cantidad:"));
        panelArriba.add(spnCantidad);
        JButton btnAgregar = new JButton("Agregar al pedido");
        panelArriba.add(btnAgregar);
        add(panelArriba, BorderLayout.NORTH);

        // --- centro: lo que lleva el pedido ---
        modeloTabla = new DefaultTableModel(new String[]{"Platillo", "Cantidad", "Precio", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- abajo: total y botones ---
        JPanel panelAbajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnGuardar = new JButton("Guardar pedido");
        panelAbajo.add(lblTotal);
        panelAbajo.add(btnQuitar);
        panelAbajo.add(btnGuardar);
        add(panelAbajo, BorderLayout.SOUTH);

        // --- eventos ---
        btnAgregar.addActionListener(e -> agregarAlPedido());
        btnQuitar.addActionListener(e -> quitarSeleccionado());
        btnGuardar.addActionListener(e -> guardarPedido());

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            cmbMesa.removeAllItems();
            for (Mesa m : pedidoService.obtenerMesasDisponibles()) {
                cmbMesa.addItem(m);
            }
            if (cmbPlatillo.getItemCount() == 0) {
                for (Platillo p : platilloService.obtenerTodos()) {
                    cmbPlatillo.addItem(p);
                }
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void agregarAlPedido() {
        try {
            Platillo platillo = (Platillo) cmbPlatillo.getSelectedItem();
            int cantidad = (Integer) spnCantidad.getValue();

            // El constructor valida que haya platillo y que la cantidad sea mayor a 0
            DetallePedido nuevo = new DetallePedido(platillo, cantidad);

            // Si el platillo ya estaba en el pedido, se suma la cantidad en lugar de repetir la linea
            for (int i = 0; i < detalles.size(); i++) {
                DetallePedido existente = detalles.get(i);
                if (existente.getPlatillo().getId() == platillo.getId()) {
                    detalles.set(i, new DetallePedido(platillo, existente.getCantidad() + cantidad));
                    spnCantidad.setValue(1);
                    refrescarTabla();
                    return;
                }
            }

            detalles.add(nuevo);
            spnCantidad.setValue(1);
            refrescarTabla();

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        }
    }

    private void quitarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            avisar("Selecciona una fila de la tabla");
            return;
        }
        detalles.remove(fila);
        refrescarTabla();
    }

    private void guardarPedido() {
        try {
            Pedido pedido = pedidoService.registrar(usuario, (Mesa) cmbMesa.getSelectedItem(), detalles);

            JOptionPane.showMessageDialog(this,
                    "Pedido #" + pedido.getId() + " guardado.\nTotal: $" + String.format("%.2f", pedido.calcularTotal()),
                    "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);

            detalles.clear();
            refrescarTabla();
            cargarDatos();   // la mesa que se uso ya no esta disponible: se actualiza la lista

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        double total = 0;
        for (DetallePedido d : detalles) {
            modeloTabla.addRow(new Object[]{
                d.getPlatillo().getNombre(), d.getCantidad(), d.getPlatillo().getPrecio(), d.getSubtotal()
            });
            total += d.getSubtotal();
        }
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
