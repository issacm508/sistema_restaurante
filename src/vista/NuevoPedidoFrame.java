package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Pedido;
import modelo.Platillo;
import modelo.Usuario;
import servicio.PedidoService;
import servicio.PlatilloService;


public class NuevoPedidoFrame extends JFrame {

    private final Usuario usuario;   // el usuario que inicio sesion (el mesero)
    private final PedidoService pedidoService = new PedidoService();
    private final PlatilloService platilloService = new PlatilloService();

    private final JComboBox<Mesa> cmbMesa = new JComboBox<>();
    private final JComboBox<Platillo> cmbPlatillo = new JComboBox<>();
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JLabel lblTotal = new JLabel("Total general: $0.00");
    private final JLabel lblPendientes = new JLabel(" ");
    // Aqui se apilan los pedidos: uno debajo del otro, cada uno con el titulo de su mesa
    private final JPanel panelPedidos = new JPanel();

    // Los pedidos que se estan armando (todavia no se guardan en la base de datos): uno por mesa.
    // La llave es el id de la mesa.
    private final Map<Integer, List<DetallePedido>> carritos = new LinkedHashMap<>();
    private final Map<Integer, Mesa> mesasConCarrito = new LinkedHashMap<>();

    public NuevoPedidoFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Nuevo pedido");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // al cerrar solo se cierra esta ventana
        setSize(900, 520);
        setLocationRelativeTo(null);

        // --- parte de arriba: mesa, platillo y cantidad ---
        JPanel panelArriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelArriba.add(new JLabel("Mesa:"));
        panelArriba.add(cmbMesa);
        panelArriba.add(new JLabel("Platillo:"));
        panelArriba.add(cmbPlatillo);
        panelArriba.add(new JLabel("Cantidad:"));
        panelArriba.add(spnCantidad);
        JButton btnAgregar = new JButton("Agregar al pedido");
        panelArriba.add(btnAgregar);

        // debajo se muestra en que mesas hay pedidos sin guardar
        JPanel panelPendientes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPendientes.add(lblPendientes);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(panelArriba, BorderLayout.NORTH);
        norte.add(panelPendientes, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        // --- centro: un bloque por mesa, todos en la misma ventana ---
        panelPedidos.setLayout(new BoxLayout(panelPedidos, BoxLayout.Y_AXIS));
        panelPedidos.setBackground(Tema.FONDO);
        JPanel envoltura = new JPanel(new BorderLayout());   // mantiene los bloques pegados arriba
        envoltura.setBackground(Tema.FONDO);
        envoltura.add(panelPedidos, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(envoltura);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.PRIMARIO, 1));
        scroll.getViewport().setBackground(Tema.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // --- abajo: total general y boton para guardar todo ---
        JPanel panelAbajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        JButton btnGuardarTodos = new JButton("Guardar todos");
        panelAbajo.add(lblTotal);
        panelAbajo.add(btnGuardarTodos);
        add(panelAbajo, BorderLayout.SOUTH);

        // --- eventos ---
        btnAgregar.addActionListener(e -> agregarAlPedido());
        btnGuardarTodos.addActionListener(e -> guardarTodos());

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            Mesa anterior = (Mesa) cmbMesa.getSelectedItem();   // para volver a elegirla si sigue libre

            List<Mesa> disponibles = pedidoService.obtenerMesasDisponibles();
            cmbMesa.removeAllItems();
            for (Mesa m : disponibles) {
                cmbMesa.addItem(m);
                if (anterior != null && m.getId() == anterior.getId()) {
                    cmbMesa.setSelectedItem(m);
                }
            }

            // Si una mesa con pedido en preparacion ya no esta disponible (otra persona la ocupo), se descarta
            carritos.keySet().removeIf(id -> disponibles.stream().noneMatch(m -> m.getId() == id));
            mesasConCarrito.keySet().removeIf(id -> !carritos.containsKey(id));

            if (cmbPlatillo.getItemCount() == 0) {
                for (Platillo p : platilloService.obtenerTodos()) {
                    cmbPlatillo.addItem(p);
                }
            }
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    // El pedido en preparacion de la mesa elegida (si todavia no tiene, se crea vacio)
    private List<DetallePedido> carritoActual() {
        Mesa mesa = (Mesa) cmbMesa.getSelectedItem();
        if (mesa == null) {
            return null;
        }
        mesasConCarrito.put(mesa.getId(), mesa);
        return carritos.computeIfAbsent(mesa.getId(), id -> new ArrayList<>());
    }

    private void agregarAlPedido() {
        List<DetallePedido> detalles = carritoActual();
        if (detalles == null) {
            avisar("Selecciona una mesa");
            return;
        }
        try {
            Platillo platillo = (Platillo) cmbPlatillo.getSelectedItem();
            int cantidad = (Integer) spnCantidad.getValue();

            // El constructor valida que haya platillo y que la cantidad sea mayor a 0
            DetallePedido nuevo = new DetallePedido(platillo, cantidad);

            // Si el platillo ya estaba en el pedido, se suma la cantidad en lugar de repetir la linea
            for (int i = 0; i < detalles.size(); i++) {
                DetallePedido existente = detalles.get(i);
                if (existente.getPlatillo().getId() == platillo.getId()) {
                    detalles.set(i, new DetallePedido(platillo, existente.getCantidad() + cantidad));
                    spnCantidad.setValue(1);
                    refrescarTabla();
                    return;
                }
            }

            detalles.add(nuevo);
            spnCantidad.setValue(1);
            refrescarTabla();

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        }
    }

    // Quita una linea del pedido de una mesa; si el pedido queda vacio, desaparece su bloque. 
    private void quitarLinea(Mesa mesa, JTable tabla) {
        int fila = tabla.getSelectedRow();
        List<DetallePedido> detalles = carritos.get(mesa.getId());
        if (fila < 0 || detalles == null) {
            avisar("Selecciona una fila de la tabla de la mesa " + mesa.getNumeroMesa());
            return;
        }
        detalles.remove(fila);
        if (detalles.isEmpty()) {
            carritos.remove(mesa.getId());
            mesasConCarrito.remove(mesa.getId());
        }
        refrescarTabla();
    }

    // Guarda solo el pedido de esa mesa. Los de las otras mesas siguen pendientes. 
    private void guardarPedido(Mesa mesa) {
        List<DetallePedido> detalles = carritos.get(mesa.getId());
        if (detalles == null || detalles.isEmpty()) {
            avisar("La mesa " + mesa.getNumeroMesa() + " no tiene platillos");
            return;
        }
        try {
            Pedido pedido = pedidoService.registrar(usuario, mesa, detalles);

            JOptionPane.showMessageDialog(this,
                    "Pedido #" + pedido.getId() + " de la mesa " + mesa.getNumeroMesa() + " guardado.\n"
                            + "Total: $" + String.format("%.2f", pedido.calcularTotal()),
                    "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);

            carritos.remove(mesa.getId());
            mesasConCarrito.remove(mesa.getId());
            cargarDatos();   // la mesa que se uso ya no esta disponible: se actualiza la lista

        } catch (IllegalArgumentException ex) {
            avisar(ex.getMessage());
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    // Guarda de una vez los pedidos de todas las mesas que tengan platillos. 
    private void guardarTodos() {
        int guardados = 0;
        double total = 0;
        StringBuilder problemas = new StringBuilder();

        for (Integer idMesa : new ArrayList<>(carritos.keySet())) {
            List<DetallePedido> detalles = carritos.get(idMesa);
            if (detalles == null || detalles.isEmpty()) {
                continue;   // esa mesa no tiene nada que guardar
            }
            Mesa mesa = mesasConCarrito.get(idMesa);
            try {
                Pedido pedido = pedidoService.registrar(usuario, mesa, detalles);
                guardados++;
                total += pedido.calcularTotal();
                carritos.remove(idMesa);
                mesasConCarrito.remove(idMesa);
            } catch (IllegalArgumentException ex) {
                // ese pedido no se pudo guardar: se queda pendiente y se sigue con los demas
                problemas.append("Mesa ").append(mesa.getNumeroMesa()).append(": ")
                        .append(ex.getMessage()).append("\n");
            } catch (SQLException ex) {
                mostrarErrorBD(ex);
                break;
            }
        }

        if (guardados == 0 && problemas.length() == 0) {
            avisar("No hay pedidos pendientes. Agrega platillos a una mesa primero");
            return;
        }

        String mensaje = guardados + " pedido(s) guardado(s). Total: $" + String.format("%.2f", total);
        if (problemas.length() > 0) {
            mensaje += "\n\nNo se pudieron guardar:\n" + problemas;
        }
        JOptionPane.showMessageDialog(this, mensaje, "Pedidos registrados",
                problemas.length() > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        cargarDatos();
    }

    //Dibuja de nuevo todos los bloques (uno por mesa con pedido pendiente). 
    private void refrescarTabla() {
        panelPedidos.removeAll();
        double totalGeneral = 0;

        for (Map.Entry<Integer, List<DetallePedido>> entrada : carritos.entrySet()) {
            List<DetallePedido> detalles = entrada.getValue();
            if (detalles.isEmpty()) {
                continue;
            }
            Mesa mesa = mesasConCarrito.get(entrada.getKey());
            panelPedidos.add(crearBloque(mesa, detalles));
            for (DetallePedido d : detalles) {
                totalGeneral += d.getSubtotal();
            }
        }

        if (panelPedidos.getComponentCount() == 0) {
            JLabel vacio = new JLabel("Elige una mesa, un platillo y pulsa \"Agregar al pedido\"");
            vacio.setFont(Tema.FUENTE);
            vacio.setForeground(Tema.TEXTO);
            vacio.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            panelPedidos.add(vacio);
        }

        lblTotal.setText(String.format("Total general: $%.2f", totalGeneral));
        actualizarPendientes();
        panelPedidos.revalidate();
        panelPedidos.repaint();
    }

    // El bloque de una mesa: titulo, tabla con sus platillos y botones propios. 
    private JPanel crearBloque(Mesa mesa, List<DetallePedido> detalles) {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"Platillo", "Cantidad", "Precio", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        double subtotalMesa = 0;
        for (DetallePedido d : detalles) {
            modelo.addRow(new Object[]{
                d.getPlatillo().getNombre(), d.getCantidad(), d.getPlatillo().getPrecio(), d.getSubtotal()
            });
            subtotalMesa += d.getSubtotal();
        }
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.estilizarTabla(tabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Tema.BORDE, 1));
        scrollTabla.getViewport().setBackground(Tema.FONDO);
        // alto justo para mostrar todas las filas (el scroll general se encarga si son muchas)
        scrollTabla.setPreferredSize(new Dimension(100, tabla.getRowHeight() * (detalles.size() + 1) + 6));

        JLabel titulo = new JLabel("Pedido de la mesa " + mesa.getNumeroMesa());
        titulo.setFont(Tema.FUENTE_NEGRITA.deriveFont(16f));
        titulo.setForeground(Tema.PRIMARIO);

        JLabel lblSubtotal = new JLabel(String.format("Total mesa %d: $%.2f", mesa.getNumeroMesa(), subtotalMesa));
        lblSubtotal.setFont(Tema.FUENTE_NEGRITA);
        lblSubtotal.setForeground(Tema.TEXTO);
        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnGuardar = new JButton("Guardar pedido de la mesa " + mesa.getNumeroMesa());
        Tema.estilizarBoton(btnQuitar);
        Tema.estilizarBoton(btnGuardar);
        btnQuitar.addActionListener(e -> quitarLinea(mesa, tabla));
        btnGuardar.addActionListener(e -> guardarPedido(mesa));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(Tema.FONDO);
        botones.add(lblSubtotal);
        botones.add(btnQuitar);
        botones.add(btnGuardar);

        JPanel bloque = new JPanel(new BorderLayout(0, 4));
        bloque.setBackground(Tema.FONDO);
        bloque.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Tema.PRIMARIO),
                BorderFactory.createEmptyBorder(8, 10, 6, 10)));
        bloque.add(titulo, BorderLayout.NORTH);
        bloque.add(scrollTabla, BorderLayout.CENTER);
        bloque.add(botones, BorderLayout.SOUTH);
        return bloque;
    }

    // Muestra en que mesas hay pedidos sin guardar, por ejemplo: "Sin guardar: Mesa 1 (2 platillos), Mesa 2 (1 platillo)"
    private void actualizarPendientes() {
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<Integer, List<DetallePedido>> entrada : carritos.entrySet()) {
            int lineas = entrada.getValue().size();
            if (lineas == 0) {
                continue;
            }
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append("Mesa ").append(mesasConCarrito.get(entrada.getKey()).getNumeroMesa())
                    .append(" (").append(lineas).append(lineas == 1 ? " platillo)" : " platillos)");
        }
        lblPendientes.setText(texto.length() == 0 ? "Sin pedidos pendientes" : "Sin guardar: " + texto);
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error con la base de datos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
