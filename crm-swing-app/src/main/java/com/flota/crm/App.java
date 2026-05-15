package com.flota.crm;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.flota.crm.views.MainFrame;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        // Setup modern Look and Feel before initializing UI
        try {
            // FlatMacLightLaf provides a very clean, rounded, modern UI similar to primefaces
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            
            // Customizing global UI variables for a 'Poseidon' like feel (modern, spacious)
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            
            // Custom colors (Poseidon often uses soft whites and crisp accents)
            UIManager.put("Button.background", new Color(59, 130, 246)); // Blue accent
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
            
        } catch (Exception ex) {
            System.err.println("Failed to initialize modern LaF");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
