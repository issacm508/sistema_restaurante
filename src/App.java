import javax.swing.SwingUtilities;
import vista.PlatillosFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlatillosFrame().setVisible(true));
    }
}