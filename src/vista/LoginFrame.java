package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;
import modelo.Usuario;
import servicio.AuthService;
import servicio.UsuariosRecientesService;

/**
 * Capa de vista: pantalla de inicio de sesion.
 * NO consulta la base de datos ni compara contrasenas: eso lo hace AuthService.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private final UsuariosRecientesService recientes = new UsuariosRecientesService();

    // Lista editable: se puede elegir un usuario recordado o escribir uno nuevo
    private final JComboBox<String> cmbUsuario = new JComboBox<>();
    private final JPasswordField txtPassword = new JPasswordField(15);   // oculta lo que se escribe

    public LoginFrame() {
        setTitle("Sistema para Restaurante - Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Tema.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 25, 30));
        setContentPane(contenido);

        // --- parte de arriba: logo y titulo ---
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        JLabel logo = Tema.logo(230);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        JLabel titulo = Tema.titulo("Iniciar sesion");
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        encabezado.add(logo);
        encabezado.add(titulo);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contenido.add(encabezado, BorderLayout.NORTH);

        // --- formulario ---
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(etiqueta("Usuario:"), gbc);
        gbc.gridx = 1;
        formulario.add(cmbUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(etiqueta("Contrasena:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtPassword, gbc);

        configurarListaUsuarios();
        Tema.estilizarCampo(txtPassword);
        cmbUsuario.setPreferredSize(new Dimension(txtPassword.getPreferredSize()));   // mismo ancho y alto
        contenido.add(formulario, BorderLayout.CENTER);

        // --- boton ---
        JButton btnIngresar = new JButton("Ingresar");
        Tema.estilizarBoton(btnIngresar);
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panelBoton.add(btnIngresar);
        contenido.add(panelBoton, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> iniciarSesion());   // evento del boton
        getRootPane().setDefaultButton(btnIngresar);           // Enter tambien ejecuta el boton

        // Al abrir la ventana, el cursor queda en el campo de usuario
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                cmbUsuario.getEditor().getEditorComponent().requestFocusInWindow();
            }
        });

        pack();                        // ajusta el tamano al contenido (incluye el logo)
        setLocationRelativeTo(null);   // centra la ventana
    }

    /** Llena la lista con los usuarios recordados y le da el estilo del tema. */
    private void configurarListaUsuarios() {
        // Flecha y colores de la lista (primero la apariencia, luego se hace editable)
        cmbUsuario.setUI(new BasicComboBoxUI() {
            @Override
            protected javax.swing.JButton createArrowButton() {
                BasicArrowButton flecha = new BasicArrowButton(BasicArrowButton.SOUTH,
                        Tema.SUPERFICIE, Tema.SUPERFICIE, Tema.PRIMARIO, Tema.SUPERFICIE);
                flecha.setBorder(BorderFactory.createEmptyBorder());
                return flecha;
            }
        });
        cmbUsuario.setEditable(true);
        cmbUsuario.setBackground(Tema.SUPERFICIE);
        cmbUsuario.setForeground(Tema.TEXTO);
        cmbUsuario.setFont(Tema.FUENTE);
        cmbUsuario.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                setFont(Tema.FUENTE);
                setBackground(seleccionado ? Tema.PRIMARIO : Tema.SUPERFICIE);
                setForeground(seleccionado ? Tema.TEXTO_SOBRE_DORADO : Tema.TEXTO);
                return this;
            }
        });
        Tema.estilizarCampo((JTextComponent) cmbUsuario.getEditor().getEditorComponent());

        // Usuarios recordados: aparecen en la lista, pero el campo empieza vacio para poder escribir otro
        for (String nombre : recientes.obtener()) {
            cmbUsuario.addItem(nombre);
        }
        cmbUsuario.setSelectedItem("");

        // Al hacer clic en el campo vacio se despliega la lista de usuarios recordados
        cmbUsuario.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (cmbUsuario.getItemCount() > 0 && !cmbUsuario.isPopupVisible() && usuarioEscrito().isEmpty()) {
                    cmbUsuario.showPopup();
                }
            }
        });

        // Al elegir (o escribir y dar Enter) en la lista, el cursor pasa a la contrasena
        cmbUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());
    }

    /** El nombre que esta escrito o elegido en la lista. */
    private String usuarioEscrito() {
        Object valor = cmbUsuario.getEditor().getItem();
        return valor == null ? "" : valor.toString();
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FUENTE_NEGRITA);
        l.setForeground(Tema.TEXTO);
        return l;
    }

    private void iniciarSesion() {
        try {
            String password = new String(txtPassword.getPassword());
            Usuario usuario = authService.login(usuarioEscrito(), password);
            recientes.recordar(usuario.getUsername());   // se recuerda solo si el login fue correcto

            JOptionPane.showMessageDialog(this,
                    "Bienvenido/a, " + usuario.getUsername() + "\nRol: " + usuario.getRol(),
                    "Acceso correcto", JOptionPane.INFORMATION_MESSAGE);

            // Abre el menu principal con las opciones segun el rol del usuario
            new MenuPrincipalFrame(usuario).setVisible(true);
            dispose();   // cierra la ventana de login

        } catch (IllegalArgumentException ex) {
            // datos vacios o credenciales incorrectas
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo iniciar sesion", JOptionPane.WARNING_MESSAGE);
            cmbUsuario.setSelectedItem("");
            txtPassword.setText("");
            cmbUsuario.getEditor().getEditorComponent().requestFocusInWindow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}