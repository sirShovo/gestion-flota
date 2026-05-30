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
        
        JButton btnNuevo = UIUtils.createPrimaryButton("+ Nuevo Conductor");
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
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNombre = new JTextField();
        JTextField txtCedula = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtLicencia = new JTextField();
        
        JLabel[] errNombre = new JLabel[1];
        JLabel[] errCedula = new JLabel[1];
        JLabel[] errTelefono = new JLabel[1];
        JLabel[] errLicencia = new JLabel[1];
        
        panel.add(UIUtils.createValidatedField("Nombre Completo:", txtNombre, l -> errNombre[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Cédula:", txtCedula, l -> errCedula[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Teléfono:", txtTelefono, l -> errTelefono[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Licencia:", txtLicencia, l -> errLicencia[0] = l));
        panel.add(Box.createVerticalStrut(15));
        
        JButton btnGuardar = UIUtils.createPrimaryButton("Guardar");
        btnGuardar.addActionListener(e -> {
            boolean valid = true;
            valid &= UIUtils.validateEmpty(txtNombre, errNombre[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtCedula, errCedula[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtTelefono, errTelefono[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtLicencia, errLicencia[0], "Obligatorio");
            
            if (!valid) return;
            
            Conductor c = new Conductor();
            c.setNombre(txtNombre.getText().trim());
            c.setCedula(txtCedula.getText().trim());
            c.setTelefono(txtTelefono.getText().trim());
            c.setLicencia(txtLicencia.getText().trim());
            
            if (conductorDAO.insertar(c)) {
                cargarDatos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Conductor guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el conductor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(btnGuardar);
        panel.add(bp);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
