package com.flota.crm.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashFrame extends JWindow {
    
    public SplashFrame() {
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        
        // Logo o Título Centrado
        JLabel lblLogo = new JLabel("CRM Gestión de Flota", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(new Color(30, 64, 175)); // Blue 800
        
        // Subtítulo
        JLabel lblSub = new JLabel("Cargando módulos...", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblLogo);
        titlePanel.add(lblSub);
        
        // Barra de progreso Soft
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(400, 10));
        progressBar.setBorderPainted(false);
        
        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(0, 50, 40, 50));
        progressPanel.add(progressBar);
        
        contentPanel.add(titlePanel, BorderLayout.CENTER);
        contentPanel.add(progressPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
        
        startTimer();
    }
    
    private void startTimer() {
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cerrar splash
                SwingUtilities.invokeLater(() -> {
                    LoginFrame login = new LoginFrame();
                    login.setVisible(true);
                });
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
