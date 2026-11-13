package vista;
 
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import modelo.Usuario;
import servicio.PermisoService;
 
public class MenuPrincipalFrame extends JFrame {
 
    private final Usuario usuario;
    private final PermisoService permisos = new PermisoService();
 
    public MenuPrincipalFrame(Usuario usuario) {
        this.usuario = usuario;
 
        setTitle("Sistema para Restaurante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);   
 
        JLabel bienvenida = new JLabel(
                "Bienvenido/a, " + usuario.getUsername() + "   |   Rol: " + usuario.getRol(),
                SwingConstants.CENTER);
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 18f));
        add(bienvenida, BorderLayout.CENTER);
 
        setJMenuBar(crearBarraMenu());
    }
 
    private JMenuBar crearBarraMenu() {
        JMenuBar barra = new JMenuBar();
 
        if (permisos.puedeAdministrar(usuario)) {
            JMenu menuAdmin = new JMenu("Administracion");
            menuAdmin.add(crearItem("Platillos", e -> new PlatillosFrame().setVisible(true)));
            menuAdmin.add(crearItem("Usuarios y roles", e -> enConstruccion("Usuarios y roles")));
            menuAdmin.add(crearItem("Reportes", e -> enConstruccion("Reportes")));
            barra.add(menuAdmin);
        }
 
        if (permisos.puedeVerMesas(usuario)) {
            JMenu menuMesas = new JMenu("Mesas");
            menuMesas.add(crearItem("Asignar mesa", e -> enConstruccion("Mesas")));
            barra.add(menuMesas);
        }
 
        if (permisos.puedeVerPedidos(usuario)) {
            JMenu menuPedidos = new JMenu("Pedidos");
            menuPedidos.add(crearItem("Registrar pedido", e -> enConstruccion("Pedidos")));
            barra.add(menuPedidos);
        }
 
        if (permisos.puedeVerCocina(usuario)) {
            JMenu menuCocina = new JMenu("Cocina");
            menuCocina.add(crearItem("Pedidos en cocina", e -> enConstruccion("Cocina")));
            barra.add(menuCocina);
        }
 
        if (permisos.puedeCobrar(usuario)) {
            JMenu menuCaja = new JMenu("Caja");
            menuCaja.add(crearItem("Cobrar pedido", e -> enConstruccion("Caja")));
            barra.add(menuCaja);
        }
 
        JMenu menuSesion = new JMenu("Sesion");
        menuSesion.add(crearItem("Cerrar sesion", e -> cerrarSesion()));
        menuSesion.add(crearItem("Salir", e -> System.exit(0)));
        barra.add(menuSesion);
 
        return barra;
    }
 
    private JMenuItem crearItem(String texto, ActionListener accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(accion);
        return item;
    }
 
    private void enConstruccion(String modulo) {
        JOptionPane.showMessageDialog(this,
                "El modulo \"" + modulo + "\" se esta desarrollando.",
                "En construccion", JOptionPane.INFORMATION_MESSAGE);
    }
 
    private void cerrarSesion() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}