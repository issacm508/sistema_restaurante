package vista;
 
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Usuario;
import servicio.AuthService;
 
public class LoginFrame extends JFrame {
 
    private final AuthService authService = new AuthService();
 
    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15); 
 
    public LoginFrame() {
        setTitle("Sistema para Restaurante - Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 240);
        setLocationRelativeTo(null); 
        setResizable(false);
 
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
 
        JLabel titulo = new JLabel("Iniciar sesion");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titulo, gbc);
 
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        add(txtUsuario, gbc);
 
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        add(txtPassword, gbc);
 
        JButton btnIngresar = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnIngresar, gbc);
 
        btnIngresar.addActionListener(e -> iniciarSesion());   
        getRootPane().setDefaultButton(btnIngresar); 
    }
 
    private void iniciarSesion() {
        try {
            String password = new String(txtPassword.getPassword());
            Usuario usuario = authService.login(txtUsuario.getText(), password);
 
            JOptionPane.showMessageDialog(this,
                    "Bienvenido/a, " + usuario.getUsername() + "\nRol: " + usuario.getRol(),
                    "Acceso correcto", JOptionPane.INFORMATION_MESSAGE);
 
            new MenuPrincipalFrame(usuario).setVisible(true);
            dispose(); 
 
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo iniciar sesion", JOptionPane.WARNING_MESSAGE);
            txtPassword.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}