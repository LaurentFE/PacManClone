package fr.LaurentFE.pacManClone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private final GamePanel mainDisplay;

    public GameFrame() {
        super("Pac-Man clone");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmClose();
            }
        });

        mainDisplay = new GamePanel();

        add(mainDisplay, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        mainDisplay.startGameThread();
    }

    // Displays an option panel to check if user really wants to exit program
    private void confirmClose() {
        int exitValue = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit ?",
                "Exit",
                JOptionPane.YES_NO_OPTION);
        if (exitValue == JOptionPane.YES_OPTION) {
            mainDisplay.stopGameThread();
            dispose();
        }
    }
}
