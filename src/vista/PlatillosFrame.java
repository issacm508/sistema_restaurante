package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.Categoria;
import modelo.Platillo;
import servicio.PlatilloService;

public class PlatillosFrame extends JFrame {

    private final PlatilloService servicio = new PlatilloService();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField txtNombre = new JTextField(14);
    private final JTextField txtPrecio = new JTextField(6);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();

    // Los platillos que se estan mostrando, en el mismo orden que las filas de la tabla
    private List<Platillo> platillos = new ArrayList<>();

    public PlatillosFrame() {
        setTitle("Platillos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 440);
        setLocationRelativeTo(null);   // centra la ventana

        // --- tabla (centro de la ventana) ---
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Categoria", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;   // la tabla es solo de lectura
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   // una fila a la vez
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Al elegir una fila, sus datos pasan al formulario para poder corregirlos
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        // --- formulario (parte de abajo): una fila de campos y otra de botones ---
        JPanel panelCampos = new JPanel(new FlowLayout());
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(txtNombre);
        panelCampos.add(new JLabel("Precio:"));
        panelCampos.add(txtPrecio);
        panelCampos.add(new JLabel("Categoria:"));
        panelCampos.add(cmbCategoria);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnAgregar.addActionListener(e -> agregarPlatillo());     // eventos de los botones
        btnGuardar.addActionListener(e -> guardarCambios());
        btnEliminar.addActionListener(e -> eliminarPlatillo());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAgregar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSur = new JPanel(new GridLayout(2, 1));
        panelSur.add(panelCampos);
        panelSur.add(panelBotones);
        add(panelSur, BorderLayout.SOUTH);

        // --- cargar datos desde la base de datos (a traves del servicio) ---
        cargarCategorias();
        refrescarTabla();
    }

    private void cargarCategorias() {
        try {
            for (Categoria c : servicio.obtenerCategorias()) {
                cmbCategoria.addItem(c);
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private Platillo platilloSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= platillos.size()) {
            return null;   // no hay ninguna fila seleccionada
        }
        return platillos.get(fila);
    }

    private void cargarSeleccionEnFormulario() {
        Platillo p = platilloSeleccionado();
        if (p == null) {
            return;
        }
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        // se elige en la lista la categoria que tiene el platillo
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId() == p.getCategoria().getId()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void agregarPlatillo() {
        try {
            servicio.agregar(txtNombre.getText(), txtPrecio.getText(),
                    (Categoria) cmbCategoria.getSelectedItem());

            limpiarFormulario();
            refrescarTabla();

        } catch (IllegalArgumentException ex) {
            // errores de validacion (nombre vacio, precio invalido...)
            avisar(ex.getMessage());
            txtPrecio.requestFocusInWindow();
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void guardarCambios() {
        try {
            servicio.actualizar(platilloSeleccionado(), txtNombre.getText(), txtPrecio.getText(),
                    (Categoria) cmbCategoria.getSelectedItem());

            limpiarFormulario();
            refrescarTabla();

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void eliminarPlatillo() {
        Platillo seleccionado = platilloSeleccionado();
        if (seleccionado == null) {
            avisar("Selecciona un platillo de la tabla");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Eliminar el platillo \"" + seleccionado.getNombre() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicio.eliminar(seleccionado);
            limpiarFormulario();
            refrescarTabla();

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        tabla.clearSelection();
    }

    private void refrescarTabla() {
        try {
            platillos = servicio.obtenerTodos();
            modeloTabla.setRowCount(0);   // vacia la tabla
            for (Platillo p : platillos) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getCategoria(), p.getPrecio()});
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Dato invalido", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
