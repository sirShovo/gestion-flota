package com.flota.crm.views;

import com.flota.crm.dao.DashboardDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class PanelDashboard extends JPanel {

    private DashboardDAO dashboardDAO;
    private JLabel lblCostos;
    private JLabel lblAsignaciones;
    private JPanel chartContainer;

    public PanelDashboard() {
        dashboardDAO = new DashboardDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        // Top Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(Color.WHITE);

        lblCostos = new JLabel("$0.00", SwingConstants.CENTER);
        JPanel card1 = createCard("Costos Mantenimiento", lblCostos, new Color(239, 68, 68)); // Red
        
        lblAsignaciones = new JLabel("0", SwingConstants.CENTER);
        JPanel card2 = createCard("Asignaciones Activas", lblAsignaciones, new Color(59, 130, 246)); // Blue
        
        cardsPanel.add(card1);
        cardsPanel.add(card2);

        // Chart Container
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(cardsPanel, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    public void cargarDatos() {
        double costos = dashboardDAO.getTotalMantenimientoCostos();
        int asignaciones = dashboardDAO.getCantidadAsignacionesActivas();
        Map<String, Integer> distribucion = dashboardDAO.getEstadoVehiculosDistribucion();

        lblCostos.setText(String.format("$%.2f", costos));
        lblAsignaciones.setText(String.valueOf(asignaciones));

        updateChart(distribucion);
    }

    private void updateChart(Map<String, Integer> distribucion) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : distribucion.entrySet()) {
            dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Vehículos por Estado",
                dataset,
                true, // legend
                true, // tooltips
                false // urls
        );

        // Styling the chart to look modern
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        
        if (distribucion.containsKey("DISPONIBLE")) plot.setSectionPaint("DISPONIBLE (" + distribucion.get("DISPONIBLE") + ")", new Color(34, 197, 94));
        if (distribucion.containsKey("EN_USO")) plot.setSectionPaint("EN_USO (" + distribucion.get("EN_USO") + ")", new Color(59, 130, 246));
        if (distribucion.containsKey("MANTENIMIENTO")) plot.setSectionPaint("MANTENIMIENTO (" + distribucion.get("MANTENIMIENTO") + ")", new Color(245, 158, 11));

        chartContainer.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
}
