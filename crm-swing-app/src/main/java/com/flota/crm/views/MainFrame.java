package com.flota.crm.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class MainFrame extends JFrame {
    
    private JPanel contentArea;
    private CardLayout cardLayout;
    
    // Panel instances
    private PanelDashboard panelDashboard;
    private PanelConductores panelConductores;
    private PanelVehiculos panelVehiculos;
    private PanelAsignaciones panelAsignaciones;
    private PanelMantenimientos panelMantenimientos;
    private PanelUsuarios panelUsuarios;

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
        rootPanel.setBackground(new Color(248, 250, 252)); // Slate 50
        
        // --- Sidebar (Navigation) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 10, 20, 10)
        ));
        
        JLabel logoLabel = new JLabel("CRM Flota");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(new Color(15, 23, 42)); // Slate 900
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        ButtonGroup menuGroup = new ButtonGroup();
        
        JToggleButton btnDashboard = createMenuButton("Dashboard", "icons/home.svg");
        JToggleButton btnConductores = createMenuButton("Conductores", "icons/users.svg");
        JToggleButton btnVehiculos = createMenuButton("Vehículos", "icons/car.svg");
        JToggleButton btnAsignaciones = createMenuButton("Asignaciones", "icons/clipboard.svg");
        JToggleButton btnMantenimientos = createMenuButton("Mantenimientos", "icons/tool.svg");
        JToggleButton btnUsuarios = createMenuButton("Usuarios", "icons/users.svg");
        
        menuGroup.add(btnDashboard);
        menuGroup.add(btnConductores);
        menuGroup.add(btnVehiculos);
        menuGroup.add(btnAsignaciones);
        menuGroup.add(btnMantenimientos);
        menuGroup.add(btnUsuarios);
        
        btnDashboard.setSelected(true);
        
        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnConductores);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnVehiculos);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnAsignaciones);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnMantenimientos);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(btnUsuarios);

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
        panelUsuarios = new PanelUsuarios();
        
        // Añadir paneles al CardLayout
        contentArea.add(panelDashboard, "Dashboard");
        contentArea.add(panelConductores, "Conductores");
        contentArea.add(panelVehiculos, "Vehiculos");
        contentArea.add(panelAsignaciones, "Asignaciones");
        contentArea.add(panelMantenimientos, "Mantenimientos");
        contentArea.add(panelUsuarios, "Usuarios");

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
        btnUsuarios.addActionListener(e -> {
            panelUsuarios.cargarDatos();
            cardLayout.show(contentArea, "Usuarios");
        });

        // Add to root
        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(contentArea, BorderLayout.CENTER);
        
        add(rootPanel);
        
        // Load initial data for Dashboard
        panelDashboard.cargarDatos();
    }
    
    private JToggleButton createMenuButton(String text, String iconPath) {
        JToggleButton btn = new JToggleButton(text);
        btn.setIcon(new FlatSVGIcon(iconPath, 20, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setMinimumSize(new Dimension(0, 45));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setMargin(new Insets(5, 15, 5, 15));
        
        // FlatLaf toggle button styling
        btn.putClientProperty("JToggleButton.buttonType", "borderless");
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(71, 85, 105)); // Slate 600
        
        return btn;
    }
}
