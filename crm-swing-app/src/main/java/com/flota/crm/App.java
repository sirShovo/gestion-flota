package com.flota.crm;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.flota.crm.views.SplashFrame;
import com.flota.crm.views.UIUtils;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            UIUtils.configureSoftUI();
        } catch (Exception ex) {
            System.err.println("Error inicializando FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            SplashFrame splash = new SplashFrame();
            splash.setVisible(true);
        });
    }
}
