package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import modelo.Usuario;
import servicio.PermisoService;

public class MenuPrincipalFrame extends JFrame {

    private static final Color GRIS = new Color(0x8A, 0x8A, 0x8A);
    private static final Color HOVER = new Color(0x2E, 0x2E, 0x2E);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM  ·  HH:mm", Locale.forLanguageTag("es-MX"));

    private static class Modulo {
        final String seccion;
        final String nombre;
        final String descripcion;
        final Supplier<JFrame> crear;

        Modulo(String seccion, String nombre, String descripcion, Supplier<JFrame> crear) {
            this.seccion = seccion;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.crear = crear;
        }
    }

    private final Usuario usuario;
    private final PermisoService permisos = new PermisoService();
    private final List<Modulo> modulos = new ArrayList<>();

    private final JLabel lblTitulo = new JLabel();
    private final JLabel lblSubtitulo = new JLabel();
    private final JLabel lblFecha = new JLabel();
    private final JPanel cuerpo = new JPanel(new BorderLayout());
    private final Timer reloj;

    private JButton botonActivo;       
    private JFrame moduloActual;       

    public MenuPrincipalFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Sistema para Restaurante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FONDO);

        armarModulos();
        add(crearBarraLateral(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);

        mostrarInicio();

        actualizarFecha();
        reloj = new Timer(1000, e -> actualizarFecha());
        reloj.start();
    }

    private void armarModulos() {
        if (permisos.puedeAdministrar(usuario)) {
            modulos.add(new Modulo("Administracion", "Platillos",
                    "Consultar y agregar los platillos del menu",
                    () -> new PlatillosFrame()));
            modulos.add(new Modulo("Administracion", "Usuarios y roles",
                    "Agregar, editar y eliminar usuarios",
                    () -> new UsuariosFrame(usuario)));
            modulos.add(new Modulo("Administracion", "Meseros",
                    "Telefono y estado de los meseros",
                    () -> new MeserosFrame()));
            modulos.add(new Modulo("Administracion", "Reportes",
                    "Ventas, platillos mas vendidos y mas",
                    () -> new ReportesFrame()));
        }
        if (permisos.puedeVerMesas(usuario)) {
            modulos.add(new Modulo("Mesas", "Asignar mesa",
                    "Ver, agregar, ocupar y liberar mesas",
                    () -> new MesasFrame()));
        }
        if (permisos.puedeVerPedidos(usuario)) {
            modulos.add(new Modulo("Pedidos", "Registrar pedido",
                    "Tomar un pedido nuevo para una mesa",
                    () -> new NuevoPedidoFrame(usuario)));
            modulos.add(new Modulo("Pedidos", "Mis pedidos",
                    "Ver y cancelar pedidos en proceso",
                    () -> new MisPedidosFrame(usuario)));
        }
        if (permisos.puedeVerCocina(usuario)) {
            modulos.add(new Modulo("Cocina", "Pedidos en cocina",
                    "Pedidos en proceso listos para preparar",
                    () -> new CocinaFrame()));
        }
        if (permisos.puedeCobrar(usuario)) {
            modulos.add(new Modulo("Caja", "Cobrar pedido",
                    "Cobrar los pedidos completados",
                    () -> new CajaFrame()));
        }
    }

    private JPanel crearBarraLateral() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.SUPERFICIE);
        barra.setPreferredSize(new Dimension(250, 0));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Tema.PRIMARIO));

        // arriba: logo y datos del usuario
        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setOpaque(false);
        arriba.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));

        JLabel logo = Tema.logo(110);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        arriba.add(logo);

        JLabel nombre = new JLabel(usuario.getUsername(), SwingConstants.CENTER);
        nombre.setFont(Tema.FUENTE_NEGRITA);
        nombre.setForeground(Tema.PRIMARIO);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        arriba.add(Box.createVerticalStrut(6));
        arriba.add(nombre);

        JLabel rol = new JLabel(String.valueOf(usuario.getRol()), SwingConstants.CENTER);
        rol.setFont(Tema.FUENTE.deriveFont(12f));
        rol.setForeground(GRIS);
        rol.setAlignmentX(Component.CENTER_ALIGNMENT);
        arriba.add(rol);
        barra.add(arriba, BorderLayout.NORTH);

        JPanel navegacion = new JPanel();
        navegacion.setLayout(new BoxLayout(navegacion, BoxLayout.Y_AXIS));
        navegacion.setOpaque(false);
        navegacion.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JButton btnInicio = crearBotonNavegacion("Inicio");
        btnInicio.addActionListener(e -> {
            seleccionar(btnInicio);
            mostrarInicio();
        });
        navegacion.add(btnInicio);
        seleccionar(btnInicio);

        String seccionActual = "";
        for (Modulo m : modulos) {
            if (!m.seccion.equals(seccionActual)) {
                seccionActual = m.seccion;
                navegacion.add(crearTituloSeccion(seccionActual));
            }
            JButton boton = crearBotonNavegacion(m.nombre);
            boton.addActionListener(e -> {
                seleccionar(boton);
                abrirModulo(m);
            });
            navegacion.add(boton);
        }
        navegacion.add(Box.createVerticalGlue());
        barra.add(navegacion, BorderLayout.CENTER);

        JPanel abajo = new JPanel();
        abajo.setLayout(new BoxLayout(abajo, BoxLayout.Y_AXIS));
        abajo.setOpaque(false);
        abajo.setBorder(BorderFactory.createEmptyBorder(8, 12, 16, 12));
        abajo.add(crearTituloSeccion("Sesion"));
        JButton btnCerrar = crearBotonNavegacion("Cerrar sesion");
        btnCerrar.addActionListener(e -> cerrarSesion());
        abajo.add(btnCerrar);
        JButton btnSalir = crearBotonNavegacion("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        abajo.add(btnSalir);
        barra.add(abajo, BorderLayout.SOUTH);

        return barra;
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel etiqueta = new JLabel(texto.toUpperCase());
        etiqueta.setFont(Tema.FUENTE.deriveFont(Font.BOLD, 11f));
        etiqueta.setForeground(GRIS);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 0));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private JButton crearBotonNavegacion(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setOpaque(true);
        boton.setFont(Tema.FUENTE_NEGRITA);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pintarBoton(boton, false);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pintarBoton(boton, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pintarBoton(boton, false);
            }
        });
        return boton;
    }

    private void seleccionar(JButton boton) {
        JButton anterior = botonActivo;
        botonActivo = boton;
        if (anterior != null) {
            pintarBoton(anterior, false);
        }
        pintarBoton(boton, false);
    }

    private void pintarBoton(JButton boton, boolean encima) {
        if (boton == botonActivo) {
            boton.setBackground(HOVER);
            boton.setForeground(Tema.PRIMARIO);
            boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, Tema.PRIMARIO),
                    BorderFactory.createEmptyBorder(7, 11, 7, 14)));
        } else {
            boton.setBackground(encima ? HOVER : Tema.SUPERFICIE);
            boton.setForeground(encima ? Tema.PRIMARIO : Tema.TEXTO);
            boton.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        }
    }


    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Tema.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        lblTitulo.setFont(Tema.FUENTE_TITULO.deriveFont(28f));
        lblTitulo.setForeground(Tema.PRIMARIO);
        lblSubtitulo.setFont(Tema.FUENTE);
        lblSubtitulo.setForeground(GRIS);
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);
        encabezado.add(textos, BorderLayout.WEST);

        lblFecha.setFont(Tema.FUENTE_NEGRITA);
        lblFecha.setForeground(Tema.TEXTO);
        lblFecha.setOpaque(true);
        lblFecha.setBackground(Tema.SUPERFICIE);
        lblFecha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDE, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        JPanel contenedorFecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        contenedorFecha.setOpaque(false);
        contenedorFecha.add(lblFecha);
        encabezado.add(contenedorFecha, BorderLayout.EAST);
        contenido.add(encabezado, BorderLayout.NORTH);

        cuerpo.setOpaque(false);
        contenido.add(cuerpo, BorderLayout.CENTER);

        return contenido;
    }

    private void mostrarInicio() {
        cerrarModuloActual();
        lblTitulo.setText("Bienvenido/a, " + usuario.getUsername());
        lblSubtitulo.setText("Elige un modulo en la barra lateral");

        JPanel inicio = new JPanel(new GridBagLayout());
        inicio.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        inicio.add(Tema.logo(360), gbc);
        ponerEnCuerpo(inicio);
    }

    private void abrirModulo(Modulo modulo) {
        cerrarModuloActual();
        lblTitulo.setText(modulo.nombre);
        lblSubtitulo.setText(modulo.descripcion);

        moduloActual = modulo.crear.get();
        Container contenido = moduloActual.getContentPane();
        Tema.aplicar(contenido);
        ponerEnCuerpo(contenido);
    }

    private void ponerEnCuerpo(Component componente) {
        cuerpo.removeAll();
        cuerpo.add(componente, BorderLayout.CENTER);
        cuerpo.revalidate();
        cuerpo.repaint();
    }

    private void cerrarModuloActual() {
        if (moduloActual == null) {
            return;
        }
        WindowEvent cierre = new WindowEvent(moduloActual, WindowEvent.WINDOW_CLOSED);
        for (WindowListener oyente : moduloActual.getWindowListeners()) {
            oyente.windowClosed(cierre);
        }
        moduloActual.dispose();
        moduloActual = null;
    }


    private void actualizarFecha() {
        String texto = LocalDateTime.now().format(FORMATO_FECHA);
        lblFecha.setText(Character.toUpperCase(texto.charAt(0)) + texto.substring(1));
    }

    private void cerrarSesion() {
        reloj.stop();
        cerrarModuloActual();
        new LoginFrame().setVisible(true);
        dispose();
    }
}