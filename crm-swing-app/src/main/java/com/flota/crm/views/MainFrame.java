package com.flota.crm.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JPanel contentArea;
    private CardLayout cardLayout;
    
    // Panel instances
    private PanelDashboard panelDashboard;
    private PanelConductores panelConductores;
    private PanelVehiculos panelVehiculos;
    private PanelAsignaciones panelAsignaciones;
    private PanelMantenimientos panelMantenimientos;

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
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        
        // Instantiate real panels
        panelDashboard = new PanelDashboard();
        panelConductores = new PanelConductores();
        panelVehiculos = new PanelVehiculos();
        panelAsignaciones = new PanelAsignaciones();
        panelMantenimientos = new PanelMantenimientos();
        
        // Añadir paneles al CardLayout
        contentArea.add(panelDashboard, "Dashboard");
        contentArea.add(panelConductores, "Conductores");
        contentArea.add(panelVehiculos, "Vehiculos");
        contentArea.add(panelAsignaciones, "Asignaciones");
        contentArea.add(panelMantenimientos, "Mantenimientos");

        // Listeners for buttons - ALWAYS reload data before showing the panel
        btnDashboard.addActionListener(e -> {
            panelDashboard.cargarDatos();
            cardLayout.show(contentArea, "Dashboard");
        });
        btnConductores.addActionListener(e -> {
            panelConductores.cargarDatos();
            cardLayout.show(contentArea, "Conductores");
        });
        btnVehiculos.addActionListener(e -> {
            panelVehiculos.cargarDatos();
            cardLayout.show(contentArea, "Vehiculos");
        });
        btnAsignaciones.addActionListener(e -> {
            panelAsignaciones.cargarDatos();
            cardLayout.show(contentArea, "Asignaciones");
        });
        btnMantenimientos.addActionListener(e -> {
            panelMantenimientos.cargarDatos();
            cardLayout.show(contentArea, "Mantenimientos");
        });

        // Add to root
        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(contentArea, BorderLayout.CENTER);
        
        add(rootPanel);
        
        // Load initial data for Dashboard
        panelDashboard.cargarDatos();
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
