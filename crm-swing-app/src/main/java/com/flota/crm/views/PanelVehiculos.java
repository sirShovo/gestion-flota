package com.flota.crm.views;

import com.flota.crm.dao.VehiculoDAO;
import com.flota.crm.models.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVehiculos extends JPanel {
    private JTable tabla;
    private DefaultTableModel tableModel;
    private VehiculoDAO vehiculoDAO;

    public PanelVehiculos() {
        vehiculoDAO = new VehiculoDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Flota de Vehículos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnNuevo = UIUtils.createPrimaryButton("+ Nuevo Vehículo");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnNuevo, BorderLayout.EAST);

        String[] columnas = {"ID", "Placa", "Marca", "Modelo", "Tipo", "Km", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(tableModel);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Vehiculo> vehiculos = vehiculoDAO.obtenerTodos();
        for (Vehiculo v : vehiculos) {
            tableModel.addRow(new Object[]{
                v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getTipo(), v.getKilometraje(), v.getEstado()
            });
        }
    }

    private void mostrarDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Vehículo", true);
        dialog.setSize(400, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtPlaca = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtTipo = new JTextField();
        JTextField txtKm = new JTextField("0.0");
        
        JLabel[] errPlaca = new JLabel[1];
        JLabel[] errMarca = new JLabel[1];
        JLabel[] errModelo = new JLabel[1];
        JLabel[] errTipo = new JLabel[1];
        JLabel[] errKm = new JLabel[1];
        
        panel.add(UIUtils.createValidatedField("Placa:", txtPlaca, l -> errPlaca[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Marca:", txtMarca, l -> errMarca[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Modelo:", txtModelo, l -> errModelo[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Tipo:", txtTipo, l -> errTipo[0] = l));
        panel.add(Box.createVerticalStrut(5));
        panel.add(UIUtils.createValidatedField("Kilometraje:", txtKm, l -> errKm[0] = l));
        panel.add(Box.createVerticalStrut(15));
        
        JButton btnGuardar = UIUtils.createPrimaryButton("Guardar");
        btnGuardar.addActionListener(e -> {
            boolean valid = true;
            valid &= UIUtils.validateEmpty(txtPlaca, errPlaca[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtMarca, errMarca[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtModelo, errModelo[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtTipo, errTipo[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtKm, errKm[0], "Obligatorio");
            
            if (!valid) return;
            
            Vehiculo v = new Vehiculo();
            v.setPlaca(txtPlaca.getText().trim());
            v.setMarca(txtMarca.getText().trim());
            v.setModelo(txtModelo.getText().trim());
            v.setTipo(txtTipo.getText().trim());
            try {
                v.setKilometraje(Double.parseDouble(txtKm.getText().trim()));
            } catch (NumberFormatException ex) {
                v.setKilometraje(0.0);
            }
            
            if (vehiculoDAO.insertar(v)) {
                cargarDatos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehículo guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el vehículo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(btnGuardar);
        panel.add(bp);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
