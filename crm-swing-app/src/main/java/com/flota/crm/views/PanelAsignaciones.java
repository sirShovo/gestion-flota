package com.flota.crm.views;

import com.flota.crm.dao.AsignacionDAO;
import com.flota.crm.dao.ConductorDAO;
import com.flota.crm.dao.VehiculoDAO;
import com.flota.crm.models.Asignacion;
import com.flota.crm.models.Conductor;
import com.flota.crm.models.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PanelAsignaciones extends JPanel {
    private JTable tabla;
    private DefaultTableModel tableModel;
    private AsignacionDAO asignacionDAO;
    private ConductorDAO conductorDAO;
    private VehiculoDAO vehiculoDAO;

    public PanelAsignaciones() {
        asignacionDAO = new AsignacionDAO();
        conductorDAO = new ConductorDAO();
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
        
        JLabel lblTitle = new JLabel("Asignaciones Activas e Historial");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnTerminar = new JButton("Finalizar Asignación Seleccionada");
        btnTerminar.setBackground(new Color(239, 68, 68)); // Rojo
        btnTerminar.setForeground(Color.WHITE);
        btnTerminar.addActionListener(e -> finalizarAsignacion());
        
        JButton btnNuevo = new JButton("+ Nueva Asignación");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        
        btnPanel.add(btnTerminar);
        btnPanel.add(btnNuevo);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        String[] columnas = {"ID", "Conductor", "Placa", "Fecha Inicio", "Fecha Fin", "Estado", "vehiculo_id"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(tableModel);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Ocultar columna vehiculo_id
        tabla.getColumnModel().getColumn(6).setMinWidth(0);
        tabla.getColumnModel().getColumn(6).setMaxWidth(0);
        tabla.getColumnModel().getColumn(6).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        UIUtils.styleTable(tabla, scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Asignacion> lista = asignacionDAO.obtenerTodas();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Asignacion a : lista) {
            String fInicio = a.getFechaAsignacion() != null ? sdf.format(a.getFechaAsignacion()) : "";
            String fFin = a.getFechaFinalizacion() != null ? sdf.format(a.getFechaFinalizacion()) : "";
            
            tableModel.addRow(new Object[]{
                a.getId(), a.getConductorNombre(), a.getVehiculoPlaca(), 
                fInicio, fFin, a.getEstado(), a.getVehiculoId()
            });
        }
    }

    private void mostrarDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Asignación", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> comboConductores = new JComboBox<>();
        List<Conductor> conductores = conductorDAO.obtenerTodos();
        for(Conductor c : conductores) {
            comboConductores.addItem(c.getId() + " - " + c.getNombre());
        }
        
        JComboBox<String> comboVehiculos = new JComboBox<>();
        List<Vehiculo> vehiculos = vehiculoDAO.obtenerTodos();
        for(Vehiculo v : vehiculos) {
            if ("DISPONIBLE".equals(v.getEstado())) {
                comboVehiculos.addItem(v.getId() + " - " + v.getPlaca() + " (" + v.getMarca() + ")");
            }
        }
        
        panel.add(new JLabel("Seleccionar Conductor:")); panel.add(comboConductores);
        panel.add(new JLabel("Seleccionar Vehículo:")); panel.add(comboVehiculos);
        
        JButton btnGuardar = new JButton("Asignar");
        btnGuardar.addActionListener(e -> {
            if (comboConductores.getSelectedItem() == null || comboVehiculos.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Debe seleccionar conductor y vehículo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String conductorStr = comboConductores.getSelectedItem().toString();
            String vehiculoStr = comboVehiculos.getSelectedItem().toString();
            
            int cId = Integer.parseInt(conductorStr.split(" - ")[0]);
            int vId = Integer.parseInt(vehiculoStr.split(" - ")[0]);
            
            Asignacion a = new Asignacion();
            a.setConductorId(cId);
            a.setVehiculoId(vId);
            
            if (asignacionDAO.insertar(a)) {
                cargarDatos();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Asignación creada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear la asignación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel()); panel.add(btnGuardar);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void finalizarAsignacion() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una asignación de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String estado = tabla.getValueAt(row, 5).toString();
        if ("FINALIZADA".equals(estado)) {
            JOptionPane.showMessageDialog(this, "La asignación ya está finalizada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idAsignacion = (int) tabla.getValueAt(row, 0);
        int idVehiculo = (int) tabla.getValueAt(row, 6);
        
        int r = JOptionPane.showConfirmDialog(this, "¿Seguro que desea finalizar esta asignación y liberar el vehículo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (asignacionDAO.finalizar(idAsignacion, idVehiculo)) {
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Asignación finalizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al finalizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
