package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimulationGUI gui = new SimulationGUI(8, 5);
                gui.setVisible(true);
            }
        });
    }
}