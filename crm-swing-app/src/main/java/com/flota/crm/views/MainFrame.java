package com.flota.crm.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("CRM - Gestión de Flota Vehicular");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        
        initComponents();
    }

    private void initComponents() {
        // Root Panel with padding
        JPanel rootPanel = new JPanel(new BorderLayout(20, 20));
        rootPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // --- Sidebar (Navigation) ---
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createTitledBorder("Menú de Navegación"));
        
        JButton btnDashboard = createMenuButton("Dashboard", true);
        JButton btnConductores = createMenuButton("Conductores", false);
        JButton btnVehiculos = createMenuButton("Vehículos", false);
        JButton btnAsignaciones = createMenuButton("Asignaciones", false);
        JButton btnMantenimientos = createMenuButton("Mantenimientos", false);
        
        sidebar.add(btnDashboard);
        sidebar.add(btnConductores);
        sidebar.add(btnVehiculos);
        sidebar.add(btnAsignaciones);
        sidebar.add(btnMantenimientos);

        // --- Main Content Area ---
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        
        // A simple title for the dashboard for now
        JLabel lblTitle = new JLabel("Bienvenido al CRM de Flota", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(51, 65, 85)); // Slate-700
        
        contentArea.add(lblTitle, BorderLayout.CENTER);

        // Add to root
        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(contentArea, BorderLayout.CENTER);
        
        add(rootPanel);
    }
    
    private JButton createMenuButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        if (!isPrimary) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(51, 65, 85));
        }
        return btn;
    }
}
