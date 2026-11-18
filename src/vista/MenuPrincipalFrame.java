package vista;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.Usuario;
import servicio.PermisoService;

//Capa de vista: ventana principal. Muestra solo los menus que el rol del usuario puede usar.

public class MenuPrincipalFrame extends JFrame {

    private final Usuario usuario;
    private final PermisoService permisos = new PermisoService();

    public MenuPrincipalFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Sistema para Restaurante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);

        // --- centro: logo y bienvenida ---
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(Tema.FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0;
        centro.add(Tema.logo(340), gbc);

        JLabel bienvenida = new JLabel("Bienvenido/a, " + usuario.getUsername(), SwingConstants.CENTER);
        bienvenida.setFont(Tema.FUENTE_TITULO);
        bienvenida.setForeground(Tema.PRIMARIO);
        gbc.gridy = 1;
        centro.add(bienvenida, gbc);

        JLabel rol = new JLabel("Rol: " + usuario.getRol(), SwingConstants.CENTER);
        rol.setFont(Tema.FUENTE);
        rol.setForeground(Tema.TEXTO);
        gbc.gridy = 2;
        centro.add(rol, gbc);

        add(centro, BorderLayout.CENTER);

        setJMenuBar(crearBarraMenu());
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setOpaque(true);
        barra.setBackground(Tema.SUPERFICIE);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Tema.PRIMARIO));

        if (permisos.puedeAdministrar(usuario)) {
            JMenu menuAdmin = crearMenu("Administracion");
            menuAdmin.add(crearItem("Platillos", e -> new PlatillosFrame().setVisible(true)));
            menuAdmin.add(crearItem("Usuarios y roles", e -> new UsuariosFrame(usuario).setVisible(true)));
            menuAdmin.add(crearItem("Reportes", e -> new ReportesFrame().setVisible(true)));
            barra.add(menuAdmin);
        }

        if (permisos.puedeVerMesas(usuario)) {
            JMenu menuMesas = crearMenu("Mesas");
            menuMesas.add(crearItem("Asignar mesa", e -> new MesasFrame().setVisible(true)));
            barra.add(menuMesas);
        }

        if (permisos.puedeVerPedidos(usuario)) {
            JMenu menuPedidos = crearMenu("Pedidos");
            menuPedidos.add(crearItem("Registrar pedido", e -> new NuevoPedidoFrame(usuario).setVisible(true)));
            barra.add(menuPedidos);
        }

        if (permisos.puedeVerCocina(usuario)) {
            JMenu menuCocina = crearMenu("Cocina");
            menuCocina.add(crearItem("Pedidos en cocina", e -> new CocinaFrame().setVisible(true)));
            barra.add(menuCocina);
        }

        if (permisos.puedeCobrar(usuario)) {
            JMenu menuCaja = crearMenu("Caja");
            menuCaja.add(crearItem("Cobrar pedido", e -> new CajaFrame().setVisible(true)));
            barra.add(menuCaja);
        }

        JMenu menuSesion = crearMenu("Sesion");
        menuSesion.add(crearItem("Cerrar sesion", e -> cerrarSesion()));
        menuSesion.add(crearItem("Salir", e -> System.exit(0)));
        barra.add(menuSesion);

        return barra;
    }

    private JMenu crearMenu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setFont(Tema.FUENTE_NEGRITA);
        menu.setForeground(Tema.PRIMARIO);
        menu.setBackground(Tema.SUPERFICIE);
        menu.setOpaque(true);
        return menu;
    }

    private JMenuItem crearItem(String texto, ActionListener accion) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(Tema.FUENTE);
        item.setForeground(Tema.TEXTO);
        item.setBackground(Tema.SUPERFICIE);
        item.setOpaque(true);
        item.addActionListener(accion);
        return item;
    }

    private void cerrarSesion() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}