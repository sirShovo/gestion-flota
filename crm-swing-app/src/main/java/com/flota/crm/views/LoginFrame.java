package com.flota.crm.views;

import com.flota.crm.dao.UsuarioDAO;
import com.flota.crm.models.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblEmailError;
    private JLabel lblPasswordError;
    private JLabel lblStatus;
    
    private UsuarioDAO usuarioDAO;
    
    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        // Cargar administrador por defecto si la base de datos está vacía
        usuarioDAO.verificarYCargarUsuarioPorDefecto();
        
        setTitle("Iniciar Sesión - CRM Flota");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Cabecera
        JLabel lblTitle = new JLabel("Bienvenido de nuevo", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42)); // Slate 900
        
        JLabel lblSubTitle = new JLabel("Ingresa a tu cuenta", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(new Color(100, 116, 139)); // Slate 500
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubTitle);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        
        JPanel pnlEmail = UIUtils.createValidatedField("Correo Electrónico", txtEmail, label -> lblEmailError = label);
        JPanel pnlPass = UIUtils.createValidatedField("Contraseña", txtPassword, label -> lblPasswordError = label);
        
        formPanel.add(pnlEmail);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(pnlPass);
        
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(239, 68, 68)); // Rojo
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblStatus);
        
        // Botón
        JButton btnLogin = UIUtils.createPrimaryButton("Iniciar Sesión");
        btnLogin.addActionListener(this::handleLogin);
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnPanel.add(btnLogin, BorderLayout.CENTER);
        
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        rootPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(rootPanel);
    }
    
    private void handleLogin(ActionEvent e) {
        boolean valid = true;
        valid &= UIUtils.validateEmpty(txtEmail, lblEmailError, "El correo es obligatorio");
        valid &= UIUtils.validateEmpty(txtPassword, lblPasswordError, "La contraseña es obligatoria");
        
        if (!valid) return;
        
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        try {
            Usuario u = usuarioDAO.autenticar(email, password);
            if (u != null) {
                // Éxito
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                });
            } else {
                lblStatus.setText("Credenciales incorrectas.");
            }
        } catch (RuntimeException ex) {
            if ("INACTIVE".equals(ex.getMessage())) {
                lblStatus.setText("El usuario está inactivo. Contacte al administrador.");
            } else {
                lblStatus.setText("Error en la conexión a BD.");
            }
        }
    }
}
