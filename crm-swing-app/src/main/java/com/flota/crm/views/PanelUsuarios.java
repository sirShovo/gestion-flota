package com.flota.crm.views;

import com.flota.crm.dao.UsuarioDAO;
import com.flota.crm.models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelUsuarios extends JPanel {
    private JTable tabla;
    private DefaultTableModel tableModel;
    private UsuarioDAO usuarioDAO;

    public PanelUsuarios() {
        usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Gestión de Usuarios");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        JButton btnNuevo = UIUtils.createPrimaryButton("+ Nuevo Usuario");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        
        JButton btnDesactivar = UIUtils.createPrimaryButton("Desactivar Seleccionado");
        btnDesactivar.setBackground(new Color(239, 68, 68)); // Rojo
        btnDesactivar.addActionListener(e -> desactivarSeleccionado());
        
        btnPanel.add(btnDesactivar);
        btnPanel.add(btnNuevo);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        String[] columnas = {"ID", "Email", "Estado", "Creado", "Actualizado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(tableModel);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        UIUtils.styleTable(tabla, scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            tableModel.addRow(new Object[]{
                u.getId(), 
                u.getEmail(), 
                u.isActivo() ? "Activo" : "Inactivo",
                u.getCreatedAt(),
                u.getUpdatedAt()
            });
        }
    }

    private void desactivarSeleccionado() {
        int selected = tabla.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selected, 0);
        int conf = JOptionPane.showConfirmDialog(this, "¿Desactivar el usuario seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (usuarioDAO.desactivar(id)) {
                JOptionPane.showMessageDialog(this, "Usuario desactivado.");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Usuario", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        
        JLabel[] errEmail = new JLabel[1];
        JLabel[] errPass = new JLabel[1];
        
        JPanel pnlEmail = UIUtils.createValidatedField("Email:", txtEmail, l -> errEmail[0] = l);
        JPanel pnlPass = UIUtils.createValidatedField("Contraseña:", txtPass, l -> errPass[0] = l);
        
        panel.add(pnlEmail);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pnlPass);
        
        JButton btnGuardar = UIUtils.createPrimaryButton("Guardar");
        btnGuardar.addActionListener(e -> {
            boolean valid = true;
            valid &= UIUtils.validateEmpty(txtEmail, errEmail[0], "Obligatorio");
            valid &= UIUtils.validateEmpty(txtPass, errPass[0], "Obligatorio");
            
            if (valid) {
                Usuario u = new Usuario();
                u.setEmail(txtEmail.getText().trim());
                u.setPasswordHash(new String(txtPass.getPassword()));
                u.setActivo(true);
                
                if (usuarioDAO.insertar(u)) {
                    cargarDatos();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Guardado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(btnGuardar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(bp);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
