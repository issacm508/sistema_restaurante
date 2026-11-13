package vista;
 
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.Mesa;
import servicio.MesaService;
 
// Capa de vista: muestra las mesas y permite cambiar su estado.
public class MesasFrame extends JFrame {
 
    private final MesaService servicio = new MesaService();
 
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField txtNumero = new JTextField(5);
 
    // Las mesas que se estan mostrando, en el mismo orden que las filas de la tabla
    private List<Mesa> mesas = new ArrayList<>();
 
    public MesasFrame() {
        setTitle("Mesas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(620, 380);
        setLocationRelativeTo(null);
 
        // --- tabla (centro) ---
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Numero de mesa", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   // una fila a la vez
        add(new JScrollPane(tabla), BorderLayout.CENTER);
 
        // --- botones (abajo) ---
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Nueva mesa (numero):"));
        panel.add(txtNumero);
 
        JButton btnAgregar = new JButton("Agregar mesa");
        JButton btnOcupar = new JButton("Marcar ocupada");
        JButton btnLiberar = new JButton("Marcar disponible");
        panel.add(btnAgregar);
        panel.add(btnOcupar);
        panel.add(btnLiberar);
        add(panel, BorderLayout.SOUTH);
 
        // --- eventos ---
        btnAgregar.addActionListener(e -> agregarMesa());
        btnOcupar.addActionListener(e -> ocuparMesa());
        btnLiberar.addActionListener(e -> liberarMesa());
 
        refrescarTabla();
    }
 
    private Mesa mesaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;   // no hay ninguna fila seleccionada
        }
        return mesas.get(fila);
    }
 
    private void agregarMesa() {
        try {
            servicio.agregar(txtNumero.getText());
            txtNumero.setText("");
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }
 
    private void ocuparMesa() {
        try {
            servicio.ocupar(mesaSeleccionada());
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }
 
    private void liberarMesa() {
        try {
            servicio.liberar(mesaSeleccionada());
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }
 
    private void refrescarTabla() {
        try {
            mesas = servicio.obtenerTodas();
            modeloTabla.setRowCount(0);
            for (Mesa m : mesas) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getNumeroMesa(), m.getEstado()});
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
