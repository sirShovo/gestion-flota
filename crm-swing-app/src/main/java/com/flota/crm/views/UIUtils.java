package com.flota.crm.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class UIUtils {
    
    // Configuración global para FlatLaf Soft UI
    public static void configureSoftUI() {
        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 15);
        UIManager.put("ProgressBar.arc", 15);
        UIManager.put("TextComponent.arc", 15);
        UIManager.put("Panel.arc", 15);
        
        UIManager.put("Button.background", new Color(59, 130, 246)); // Azul moderno
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        
        UIManager.put("TextField.margin", new Insets(8, 12, 8, 12));
        UIManager.put("PasswordField.margin", new Insets(8, 12, 8, 12));
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 0);
    }

    public static JPanel createValidatedField(String labelText, JTextField textField, Consumer<JLabel> errorLabelConsumer) {
        JPanel container = new JPanel(new BorderLayout(5, 5));
        container.setOpaque(false);
        
        JLabel lblTitulo = new JLabel(labelText);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(71, 85, 105)); // Slate 600
        
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel lblError = new JLabel(" "); // Espacio reservado
        lblError.setForeground(new Color(239, 68, 68)); // Red 500
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        
        if (errorLabelConsumer != null) {
            errorLabelConsumer.accept(lblError);
        }
        
        container.add(lblTitulo, BorderLayout.NORTH);
        container.add(textField, BorderLayout.CENTER);
        container.add(lblError, BorderLayout.SOUTH);
        
        return container;
    }
    
    public static boolean validateEmpty(JTextField field, JLabel errorLabel, String message) {
        if (field.getText().trim().isEmpty()) {
            errorLabel.setText(message);
            field.putClientProperty("JComponent.outline", "error"); // FlatLaf error border
            return false;
        } else {
            errorLabel.setText(" ");
            field.putClientProperty("JComponent.outline", null);
            return true;
        }
    }
    
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }
}
