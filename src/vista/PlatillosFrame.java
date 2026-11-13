package vista;
 
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelo.Categoria;
import modelo.Platillo;
import servicio.PlatilloService;
 
public class PlatillosFrame extends JFrame {
 
    private final PlatilloService servicio = new PlatilloService();
 
    private final DefaultTableModel modeloTabla;
    private final JTextField txtNombre = new JTextField(14);
    private final JTextField txtPrecio = new JTextField(6);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
 
    public PlatillosFrame() {
        setTitle("Platillos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 400);
        setLocationRelativeTo(null);  
 
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Categoria", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;   
            }
        };
        JTable tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
 
        JPanel panelFormulario = new JPanel(new FlowLayout());
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Precio:"));
        panelFormulario.add(txtPrecio);
        panelFormulario.add(new JLabel("Categoria:"));
        panelFormulario.add(cmbCategoria);
 
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarPlatillo());   
        panelFormulario.add(btnAgregar);
 
        add(panelFormulario, BorderLayout.SOUTH);
 
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
 
    private void agregarPlatillo() {
        try {
            servicio.agregar(txtNombre.getText(), txtPrecio.getText(),
                    (Categoria) cmbCategoria.getSelectedItem());
 
            txtNombre.setText("");
            txtPrecio.setText("");
            refrescarTabla();
 
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dato invalido", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }
 
    private void refrescarTabla() {
        try {
            modeloTabla.setRowCount(0);  
            for (Platillo p : servicio.obtenerTodos()) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getCategoria(), p.getPrecio()});
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }
 
    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
