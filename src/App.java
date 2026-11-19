import javax.swing.SwingUtilities;
import vista.LoginFrame;
import vista.Tema;

public class App {
    public static void main(String[] args) {
        Tema.instalar();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
