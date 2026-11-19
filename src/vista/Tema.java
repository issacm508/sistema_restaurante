package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;


public final class Tema {

    // Paleta (cambia estos valores para probar otros colores)
    public static final Color FONDO = new Color(0x12, 0x12, 0x12);            // negro
    public static final Color SUPERFICIE = new Color(0x1E, 0x1E, 0x1E);       // gris muy oscuro (campos, filas)
    public static final Color FILA_BASE = new Color(0x16, 0x16, 0x16);
    public static final Color BORDE = new Color(0x3A, 0x3A, 0x3A);
    public static final Color PRIMARIO = new Color(0xC9, 0xA2, 0x4B);         // dorado
    public static final Color PRIMARIO_CLARO = new Color(0xE3, 0xC1, 0x6F);   // dorado claro (al pasar el mouse)
    public static final Color TEXTO = new Color(0xF5, 0xEF, 0xE0);            // blanco calido
    public static final Color TEXTO_SOBRE_DORADO = new Color(0x12, 0x12, 0x12);

    // Fuentes
    public static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    // Ruta del logo (relativa a la carpeta del proyecto)
    private static final String RUTA_LOGO = "recursos/logo.png";

    private Tema() {
        // clase de utilidades: no se crean objetos
    }

    public static void instalar() {
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("OptionPane.messageFont", FUENTE);
        UIManager.put("OptionPane.buttonFont", FUENTE_NEGRITA);
        UIManager.put("Panel.background", FONDO);

        // Botones de los avisos: dorados, como los de la aplicacion
        UIManager.put("ButtonUI", "javax.swing.plaf.basic.BasicButtonUI");
        UIManager.put("Button.background", PRIMARIO);
        UIManager.put("Button.foreground", TEXTO_SOBRE_DORADO);
        UIManager.put("Button.select", PRIMARIO_CLARO);
        UIManager.put("Button.focus", TEXTO_SOBRE_DORADO);
        UIManager.put("Button.font", FUENTE_NEGRITA);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(8, 24, 8, 24));
    }

    // Boton dorado con letras negras y efecto al pasar el mouse. 
    public static void estilizarBoton(JButton boton) {
        boton.setUI(new BasicButtonUI());   // asi Windows respeta el color de fondo
        boton.setBackground(PRIMARIO);
        boton.setForeground(TEXTO_SOBRE_DORADO);
        boton.setFont(FUENTE_NEGRITA);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(PRIMARIO_CLARO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(PRIMARIO);
            }
        });
    }

    // Campo de texto oscuro con borde dorado y letras claras. 
    public static void estilizarCampo(JTextComponent campo) {
        campo.setFont(FUENTE);
        campo.setBackground(SUPERFICIE);
        campo.setForeground(TEXTO);
        campo.setCaretColor(PRIMARIO);
        Border linea = BorderFactory.createLineBorder(PRIMARIO, 1);
        Border margen = BorderFactory.createEmptyBorder(5, 8, 5, 8);
        campo.setBorder(BorderFactory.createCompoundBorder(linea, margen));
    }

    //Tabla oscura con encabezado dorado y filas alternadas. 
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE);
        tabla.setBackground(FILA_BASE);
        tabla.setForeground(TEXTO);
        tabla.setRowHeight(28);
        tabla.setGridColor(BORDE);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(PRIMARIO);
        tabla.setSelectionForeground(TEXTO_SOBRE_DORADO);

        // Filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(t, valor, seleccionada, foco, fila, columna);
                if (!seleccionada) {
                    c.setBackground(fila % 2 == 0 ? FILA_BASE : SUPERFICIE);
                    c.setForeground(TEXTO);
                }
                return c;
            }
        });

        // Las columnas numericas usan el mismo estilo
        tabla.setDefaultRenderer(Number.class, tabla.getDefaultRenderer(Object.class));

        // Encabezado
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(PRIMARIO);
        encabezado.setForeground(TEXTO_SOBRE_DORADO);
        encabezado.setFont(FUENTE_NEGRITA);
        encabezado.setReorderingAllowed(false);
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setBackground(PRIMARIO);
        centrado.setForeground(TEXTO_SOBRE_DORADO);
        centrado.setFont(FUENTE_NEGRITA);
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        encabezado.setDefaultRenderer(centrado);
    }

    //Lista desplegable oscura con flecha dorada. 
    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                BasicArrowButton flecha = new BasicArrowButton(BasicArrowButton.SOUTH,
                        SUPERFICIE, SUPERFICIE, PRIMARIO, SUPERFICIE);
                flecha.setBorder(BorderFactory.createEmptyBorder());
                return flecha;
            }

            // Se pintan a mano el fondo y el texto del valor elegido, para que siempre se vean oscuros
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle area, boolean foco) {
                g.setColor(SUPERFICIE);
                g.fillRect(area.x, area.y, area.width, area.height);
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle area, boolean foco) {
                Object valor = comboBox.getSelectedItem();
                String texto = (valor == null) ? "" : valor.toString();
                g.setColor(TEXTO);
                g.setFont(FUENTE);
                FontMetrics medidas = g.getFontMetrics();
                int y = area.y + (area.height + medidas.getAscent() - medidas.getDescent()) / 2;
                g.drawString(texto, area.x + 6, y);
            }
        });
        combo.setBackground(SUPERFICIE);
        combo.setForeground(TEXTO);
        combo.setFont(FUENTE);
        combo.setBorder(BorderFactory.createLineBorder(PRIMARIO, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                setFont(FUENTE);
                setBackground(seleccionado ? PRIMARIO : SUPERFICIE);
                setForeground(seleccionado ? TEXTO_SOBRE_DORADO : TEXTO);
                return this;
            }
        });
    }

    //Contador (cantidad) con el campo oscuro y borde dorado. 
    public static void estilizarSpinner(JSpinner spinner) {
        spinner.setBackground(SUPERFICIE);
        spinner.setBorder(BorderFactory.createLineBorder(PRIMARIO, 1));
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
            JTextComponent campo = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            campo.setBackground(SUPERFICIE);
            campo.setForeground(TEXTO);
            campo.setFont(FUENTE);
            campo.setCaretColor(PRIMARIO);
        }
    }

    //Aplica el tema a una ventana completa: recorre todos sus componentes y les da el estilo.Se llama una sola vez, al final del constructor de la ventana: Tema.aplicar(this);
    
    public static void aplicar(Container ventana) {
        if (ventana instanceof RootPaneContainer) {
            Container contenido = ((RootPaneContainer) ventana).getContentPane();
            contenido.setBackground(FONDO);
            aplicarA(contenido);
        } else {
            aplicarA(ventana);
        }
    }

    @SuppressWarnings("unchecked")
    private static void aplicarA(Component c) {
        if (c instanceof JButton) {
            estilizarBoton((JButton) c);
            return;
        }
        if (c instanceof JTable) {
            estilizarTabla((JTable) c);
            return;
        }
        if (c instanceof JComboBox) {
            estilizarCombo((JComboBox<Object>) c);
            return;
        }
        if (c instanceof JSpinner) {
            estilizarSpinner((JSpinner) c);
            return;
        }
        if (c instanceof JTextComponent) {
            estilizarCampo((JTextComponent) c);
            return;
        }
        if (c instanceof JLabel) {
            c.setForeground(TEXTO);
        } else if (c instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) c;
            scroll.getViewport().setBackground(FONDO);
            scroll.setBorder(BorderFactory.createLineBorder(PRIMARIO, 1));
        } else if (c instanceof JPanel || c instanceof JSplitPane) {
            c.setBackground(FONDO);
        }
        if (c instanceof Container) {
            for (Component hijo : ((Container) c).getComponents()) {
                aplicarA(hijo);
            }
        }
    }

    /// Etiqueta de titulo en dorado. 
    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(FUENTE_TITULO);
        etiqueta.setForeground(PRIMARIO);
        return etiqueta;
    }

    
     //Etiqueta con el logo escalado al ancho indicado.Si no encuentra el archivo recursos/logo.png, devuelve una etiqueta vacia (la app sigue funcionando).
  
    public static JLabel logo(int ancho) {
        File archivo = new File(RUTA_LOGO);
        if (!archivo.exists()) {
            return new JLabel();
        }
        ImageIcon original = new ImageIcon(archivo.getPath());
        if (original.getIconWidth() <= 0) {
            return new JLabel();
        }
        int alto = original.getIconHeight() * ancho / original.getIconWidth();
        Image escalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        JLabel etiqueta = new JLabel(new ImageIcon(escalada));
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        return etiqueta;
    }
}
