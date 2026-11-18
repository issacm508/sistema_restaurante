package vista;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Usuario;
import servicio.AuthService;

/**
 * Capa de vista: pantalla de inicio de sesion.
 * NO consulta la base de datos ni compara contrasenas: eso lo hace AuthService.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private final JTextField txtUsuario = new JTextField(15);
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
        formulario.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(etiqueta("Contrasena:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtPassword, gbc);

        Tema.estilizarCampo(txtUsuario);
        Tema.estilizarCampo(txtPassword);
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

        pack();                        // ajusta el tamano al contenido (incluye el logo)
        setLocationRelativeTo(null);   // centra la ventana
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
            Usuario usuario = authService.login(txtUsuario.getText(), password);

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
            txtUsuario.setText("");
            txtPassword.setText("");
            txtUsuario.requestFocusInWindow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}