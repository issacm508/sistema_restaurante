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
import javax.swing.table.DefaultTableModel;
import modelo.Reporte;
import modelo.TipoReporte;
import servicio.ReporteService;

// Capa de vista: muestra los reportes en una tabla.Solo habla con ReporteService; no conoce el DAO ni escribe SQL.
 
public class ReportesFrame extends JFrame {

    private final ReporteService servicio = new ReporteService();

    private final JComboBox<TipoReporte> cmbReporte = new JComboBox<>(TipoReporte.values());
    private final JTable tabla = new JTable();
    private final JLabel lblResumen = new JLabel(" ");

    public ReportesFrame() {
        setTitle("Reportes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(700, 420);
        setLocationRelativeTo(null);

        // --- arriba: elegir el reporte ---
        JPanel panelArriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelArriba.add(new JLabel("Reporte:"));
        panelArriba.add(cmbReporte);
        JButton btnActualizar = new JButton("Actualizar");
        panelArriba.add(btnActualizar);
        add(panelArriba, BorderLayout.NORTH);

        // --- centro: la tabla con los resultados ---
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- abajo: cuantas filas hay ---
        JPanel panelAbajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAbajo.add(lblResumen);
        add(panelAbajo, BorderLayout.SOUTH);

        // --- eventos: al cambiar de reporte o pulsar el boton, se vuelve a generar ---
        cmbReporte.addActionListener(e -> generar());
        btnActualizar.addActionListener(e -> generar());

        generar();   // muestra el primer reporte al abrir la ventana
    }

    private void generar() {
        try {
            Reporte reporte = servicio.generar((TipoReporte) cmbReporte.getSelectedItem());

            DefaultTableModel modelo = new DefaultTableModel(reporte.getColumnas(), 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;   // la tabla es solo de lectura
                }
            };
            for (Object[] fila : reporte.getFilas()) {
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);

            lblResumen.setText(reporte.getTitulo() + ": " + reporte.getFilas().size() + " fila(s)");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}