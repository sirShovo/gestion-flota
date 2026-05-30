package com.flota.crm.views;

import com.flota.crm.dao.MantenimientoDAO;
import com.flota.crm.dao.VehiculoDAO;
import com.flota.crm.models.Mantenimiento;
import com.flota.crm.models.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PanelMantenimientos extends JPanel {
    private JTable tabla;
    private DefaultTableModel tableModel;
    private MantenimientoDAO mantenimientoDAO;
    private VehiculoDAO vehiculoDAO;

    public PanelMantenimientos() {
        mantenimientoDAO = new MantenimientoDAO();
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
        
        JLabel lblTitle = new JLabel("Registro de Mantenimientos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnTerminar = new JButton("Marcar como Terminado");
        btnTerminar.setBackground(new Color(34, 197, 94)); // Verde
        btnTerminar.setForeground(Color.WHITE);
        btnTerminar.addActionListener(e -> finalizarMantenimiento());
        
        JButton btnNuevo = new JButton("+ Enviar a Taller");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        
        btnPanel.add(btnTerminar);
        btnPanel.add(btnNuevo);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        String[] columnas = {"ID", "Placa", "Tipo", "Descripción", "Costo", "Fecha", "Estado", "vehiculo_id"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(tableModel);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Ocultar columna vehiculo_id
        tabla.getColumnModel().getColumn(7).setMinWidth(0);
        tabla.getColumnModel().getColumn(7).setMaxWidth(0);
        tabla.getColumnModel().getColumn(7).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        UIUtils.styleTable(tabla, scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Mantenimiento> lista = mantenimientoDAO.obtenerTodos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Mantenimiento m : lista) {
            String fecha = m.getFecha() != null ? sdf.format(m.getFecha()) : "";
            
            tableModel.addRow(new Object[]{
                m.getId(), m.getVehiculoPlaca(), m.getTipo(), m.getDescripcion(), 
                "$" + m.getCosto(), fecha, m.getEstado(), m.getVehiculoId()
            });
        }
    }

    private void mostrarDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Mantenimiento", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> comboVehiculos = new JComboBox<>();
        List<Vehiculo> vehiculos = vehiculoDAO.obtenerTodos();
        for(Vehiculo v : vehiculos) {
            if ("DISPONIBLE".equals(v.getEstado())) {
                comboVehiculos.addItem(v.getId() + " - " + v.getPlaca() + " (" + v.getMarca() + ")");
            }
        }
        
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"PREVENTIVO", "CORRECTIVO"});
        JTextField txtDescripcion = new JTextField();
        JTextField txtCosto = new JTextField("0.0");
        
        panel.add(new JLabel("Vehículo Disponible:")); panel.add(comboVehiculos);
        panel.add(new JLabel("Tipo:")); panel.add(comboTipo);
        panel.add(new JLabel("Descripción:")); panel.add(txtDescripcion);
        panel.add(new JLabel("Costo Aprox ($):")); panel.add(txtCosto);
        
        JButton btnGuardar = new JButton("Enviar a Taller");
        btnGuardar.addActionListener(e -> {
            if (comboVehiculos.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Debe seleccionar un vehículo disponible.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String vehiculoStr = comboVehiculos.getSelectedItem().toString();
            int vId = Integer.parseInt(vehiculoStr.split(" - ")[0]);
            
            Mantenimiento m = new Mantenimiento();
            m.setVehiculoId(vId);
            m.setTipo(comboTipo.getSelectedItem().toString());
            m.setDescripcion(txtDescripcion.getText());
            try {
                m.setCosto(Double.parseDouble(txtCosto.getText()));
            } catch (Exception ex) {
                m.setCosto(0.0);
            }
            
            if (mantenimientoDAO.insertar(m)) {
                cargarDatos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehículo enviado a taller con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al procesar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel()); panel.add(btnGuardar);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void finalizarMantenimiento() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un mantenimiento en curso.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String estado = tabla.getValueAt(row, 6).toString();
        if ("FINALIZADO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Este mantenimiento ya fue finalizado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idMantenimiento = (int) tabla.getValueAt(row, 0);
        int idVehiculo = (int) tabla.getValueAt(row, 7);
        
        int r = JOptionPane.showConfirmDialog(this, "¿El mantenimiento está listo y el vehículo regresa a disponibilidad?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (mantenimientoDAO.finalizar(idMantenimiento, idVehiculo)) {
                cargarDatos();
                JOptionPane.showMessageDialog(this, "El vehículo ha regresado a la flota.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
