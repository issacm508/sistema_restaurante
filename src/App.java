import javax.swing.SwingUtilities;
import vista.LoginFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}