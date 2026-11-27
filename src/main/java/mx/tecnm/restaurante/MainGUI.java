package mx.tecnm.restaurante;

import javax.swing.SwingUtilities;
import mx.tecnm.restaurante.ui.MenuGrafico;

public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGrafico ventana = new MenuGrafico();
            ventana.setVisible(true);
        });
    }
}