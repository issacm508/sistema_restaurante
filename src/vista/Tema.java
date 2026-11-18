package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

/**
 * Capa de vista: colores, fuentes y estilos que comparten todas las ventanas.
 * Tema negro con dorado, a juego con el logo de La Comarca Mexicana.
 * Para cambiar la apariencia de toda la aplicacion basta con editar esta clase.
 */
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

    /** Boton dorado con letras negras y efecto al pasar el mouse. */
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

    /** Campo de texto oscuro con borde dorado y letras claras. */
    public static void estilizarCampo(JTextComponent campo) {
        campo.setFont(FUENTE);
        campo.setBackground(SUPERFICIE);
        campo.setForeground(TEXTO);
        campo.setCaretColor(PRIMARIO);
        Border linea = BorderFactory.createLineBorder(PRIMARIO, 1);
        Border margen = BorderFactory.createEmptyBorder(5, 8, 5, 8);
        campo.setBorder(BorderFactory.createCompoundBorder(linea, margen));
    }

    /** Tabla oscura con encabezado dorado y filas alternadas. */
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

    /** Etiqueta de titulo en dorado. */
    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(FUENTE_TITULO);
        etiqueta.setForeground(PRIMARIO);
        return etiqueta;
    }

    /**
     * Etiqueta con el logo escalado al ancho indicado.
     * Si no encuentra el archivo recursos/logo.png, devuelve una etiqueta vacia (la app sigue funcionando).
     */
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