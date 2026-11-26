package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
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
import modelo.Mesero;
import servicio.MeseroService;

public class MeserosFrame extends JFrame {

    private static final String ACTIVO = "Activo";
    private static final String INACTIVO = "Inactivo";

    private final MeseroService servicio = new MeseroService();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField txtNombre = new JTextField(10);
    private final JTextField txtApellido = new JTextField(10);
    private final JTextField txtTelefono = new JTextField(10);
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{ACTIVO, INACTIVO});

   
    private List<Mesero> meseros = new ArrayList<>();

    public MeserosFrame() {
        setTitle("Meseros");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
        setSize(780, 440);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FONDO);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Telefono", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.PRIMARIO, 1));
        scroll.getViewport().setBackground(Tema.FONDO);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel campos = new JPanel(new FlowLayout());
        campos.setBackground(Tema.FONDO);
        campos.add(etiqueta("Nombre:"));
        campos.add(txtNombre);
        campos.add(etiqueta("Apellido:"));
        campos.add(txtApellido);
        campos.add(etiqueta("Telefono:"));
        campos.add(txtTelefono);
        campos.add(etiqueta("Estado:"));
        campos.add(cmbEstado);

        JLabel nota = etiqueta("Telefono: 10 digitos (opcional). Un mesero inactivo no puede tomar pedidos.");
        nota.setHorizontalAlignment(JLabel.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        botones.setBackground(Tema.FONDO);
        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnLimpiar = new JButton("Limpiar");
        Tema.estilizarBoton(btnGuardar);
        Tema.estilizarBoton(btnLimpiar);
        botones.add(btnGuardar);
        botones.add(btnLimpiar);

        JPanel sur = new JPanel(new GridLayout(3, 1));
        sur.setBackground(Tema.FONDO);
        sur.add(campos);
        sur.add(nota);
        sur.add(botones);
        add(sur, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                llenarFormulario();
            }
        });

        refrescarTabla();
    }

    private JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Tema.FUENTE_NEGRITA);
        etiqueta.setForeground(Tema.TEXTO);
        return etiqueta;
    }

    private Mesero meseroSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;   
        }
        return meseros.get(fila);
    }

    private void guardar() {
        try {
            servicio.actualizar(meseroSeleccionado(), txtNombre.getText(), txtApellido.getText(),
                    txtTelefono.getText(), ACTIVO.equals(cmbEstado.getSelectedItem()));
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void llenarFormulario() {
        Mesero m = meseroSeleccionado();
        if (m == null) {
            return;
        }
        txtNombre.setText(m.getNombre());
        txtApellido.setText(m.getApellido());
        txtTelefono.setText(m.getTelefono() == null ? "" : m.getTelefono());
        cmbEstado.setSelectedItem(m.isActivo() ? ACTIVO : INACTIVO);
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    private void refrescarTabla() {
        try {
            meseros = servicio.obtenerTodos();
            modeloTabla.setRowCount(0);
            for (Mesero m : meseros) {
                modeloTabla.addRow(new Object[]{
                    m.getId(), m.getNombre(), m.getApellido(),
                    m.getTelefono() == null ? "" : m.getTelefono(),
                    m.isActivo() ? ACTIVO : INACTIVO});
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}