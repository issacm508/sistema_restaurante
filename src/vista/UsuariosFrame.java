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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.Rol;
import modelo.Usuario;
import servicio.UsuarioService;


 //Capa de vista: administracion de usuarios y roles (solo Super Administrador).
public class UsuariosFrame extends JFrame {

    private final UsuarioService servicio = new UsuarioService();
    private final Usuario usuarioActual;   // quien inicio sesion

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField txtUsuario = new JTextField(12);
    private final JPasswordField txtPassword = new JPasswordField(12);
    private final JComboBox<Rol> cmbRol = new JComboBox<>();

    // Los usuarios que se estan mostrando, en el mismo orden que las filas de la tabla
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuariosFrame(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;

        setTitle("Usuarios y roles");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(760, 440);
        setLocationRelativeTo(null);

        // --- tabla (centro) ---
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Usuario", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- formulario y botones (abajo) ---
        JPanel campos = new JPanel(new FlowLayout());
        campos.add(new JLabel("Usuario:"));
        campos.add(txtUsuario);
        campos.add(new JLabel("Contrasena:"));
        campos.add(txtPassword);
        campos.add(new JLabel("Rol:"));
        campos.add(cmbRol);

        JLabel nota = new JLabel(
                "Al editar un usuario, deja la contrasena vacia para no cambiarla.", JLabel.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar");
        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        botones.add(btnAgregar);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel sur = new JPanel(new GridLayout(3, 1));
        sur.add(campos);
        sur.add(nota);
        sur.add(botones);
        add(sur, BorderLayout.SOUTH);

        // --- eventos ---
        btnAgregar.addActionListener(e -> agregar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Al elegir una fila, sus datos pasan al formulario para poder editarlos
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                llenarFormulario();
            }
        });

        cargarRoles();
        refrescarTabla();
    }

    private Usuario usuarioSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return usuarios.get(fila);
    }

    private void agregar() {
        try {
            servicio.agregar(txtUsuario.getText(), contrasenaEscrita(), (Rol) cmbRol.getSelectedItem());
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void guardar() {
        try {
            servicio.actualizar(usuarioSeleccionado(), txtUsuario.getText(), contrasenaEscrita(),
                    (Rol) cmbRol.getSelectedItem(), usuarioActual);
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void eliminar() {
        Usuario seleccionado = usuarioSeleccionado();
        if (seleccionado != null) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Eliminar al usuario " + seleccionado.getUsername() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            // Si no hay nada seleccionado, el servicio responde con el aviso correspondiente
            servicio.eliminar(seleccionado, usuarioActual);
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private String contrasenaEscrita() {
        return new String(txtPassword.getPassword());
    }

    private void llenarFormulario() {
        Usuario u = usuarioSeleccionado();
        if (u == null) {
            return;
        }
        txtUsuario.setText(u.getUsername());
        txtPassword.setText("");   // la contrasena actual no se muestra
        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getId() == u.getRol().getId()) {
                cmbRol.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtUsuario.setText("");
        txtPassword.setText("");
        if (cmbRol.getItemCount() > 0) {
            cmbRol.setSelectedIndex(0);
        }
    }

    private void cargarRoles() {
        try {
            for (Rol r : servicio.obtenerRoles()) {
                cmbRol.addItem(r);
            }
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void refrescarTabla() {
        try {
            usuarios = servicio.obtenerTodos();
            modeloTabla.setRowCount(0);
            for (Usuario u : usuarios) {
                modeloTabla.addRow(new Object[]{u.getId(), u.getUsername(), u.getRol().getNombre()});
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