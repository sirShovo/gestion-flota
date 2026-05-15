package com.flota.crm.views;

import com.flota.crm.dao.ConductorDAO;
import com.flota.crm.models.Conductor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelConductores extends JPanel {
    private JTable tablaConductores;
    private DefaultTableModel tableModel;
    private ConductorDAO conductorDAO;

    public PanelConductores() {
        conductorDAO = new ConductorDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Gestión de Conductores");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnNuevo = new JButton("+ Nuevo Conductor");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnNuevo, BorderLayout.EAST);

        // --- Table Section ---
        String[] columnas = {"ID", "Nombre", "Cédula", "Teléfono", "Licencia"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaConductores = new JTable(tableModel);
        tablaConductores.setRowHeight(30);
        tablaConductores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaConductores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tablaConductores);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Conductor> conductores = conductorDAO.obtenerTodos();
        for (Conductor c : conductores) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNombre(), c.getCedula(), c.getTelefono(), c.getLicencia()
            });
        }
    }

    private void mostrarDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Conductor", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNombre = new JTextField();
        JTextField txtCedula = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtLicencia = new JTextField();
        
        panel.add(new JLabel("Nombre Completo:")); panel.add(txtNombre);
        panel.add(new JLabel("Cédula:")); panel.add(txtCedula);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
        panel.add(new JLabel("Licencia:")); panel.add(txtLicencia);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            Conductor c = new Conductor();
            c.setNombre(txtNombre.getText());
            c.setCedula(txtCedula.getText());
            c.setTelefono(txtTelefono.getText());
            c.setLicencia(txtLicencia.getText());
            
            if (conductorDAO.insertar(c)) {
                cargarDatos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Conductor guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el conductor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel()); panel.add(btnGuardar);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
