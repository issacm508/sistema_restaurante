package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TicketDialog extends JDialog {

    private final JTextArea area = new JTextArea();
    private final int numeroTicket;

    public TicketDialog(Frame padre, String textoTicket, int numeroTicket) {
        super(padre, "Ticket #" + numeroTicket, true);
        this.numeroTicket = numeroTicket;

        setSize(400, 460);
        setLocationRelativeTo(padre);

        // El ticket se ve como papel: fondo blanco y letra de maquina (monoespaciada)
        area.setText(textoTicket);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBackground(Color.WHITE);
        area.setForeground(Color.BLACK);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setCaretPosition(0);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnGuardar = new JButton("Guardar como .txt");
        JButton btnCerrar = new JButton("Cerrar");
        panel.add(btnImprimir);
        panel.add(btnGuardar);
        panel.add(btnCerrar);
        add(panel, BorderLayout.SOUTH);

        btnImprimir.addActionListener(e -> imprimir());
        btnGuardar.addActionListener(e -> guardar());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void imprimir() {
        try {
            area.print();  
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo imprimir: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar() {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("ticket_" + numeroTicket + ".txt"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Files.write(selector.getSelectedFile().toPath(),
                    area.getText().getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "Ticket guardado", "Listo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}