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
        
        JButton btnNuevo = new JButton("+ Nuevo Vehículo");
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
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtPlaca = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtTipo = new JTextField();
        JTextField txtKm = new JTextField("0.0");
        
        panel.add(new JLabel("Placa:")); panel.add(txtPlaca);
        panel.add(new JLabel("Marca:")); panel.add(txtMarca);
        panel.add(new JLabel("Modelo:")); panel.add(txtModelo);
        panel.add(new JLabel("Tipo:")); panel.add(txtTipo);
        panel.add(new JLabel("Kilometraje:")); panel.add(txtKm);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            Vehiculo v = new Vehiculo();
            v.setPlaca(txtPlaca.getText());
            v.setMarca(txtMarca.getText());
            v.setModelo(txtModelo.getText());
            v.setTipo(txtTipo.getText());
            try {
                v.setKilometraje(Double.parseDouble(txtKm.getText()));
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
        
        panel.add(new JLabel()); panel.add(btnGuardar);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
