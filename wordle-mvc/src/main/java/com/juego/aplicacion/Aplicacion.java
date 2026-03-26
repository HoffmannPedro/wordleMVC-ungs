package com.juego.aplicacion;

import javax.swing.SwingUtilities;

import com.juego.controlador.ControladorJuego;
import com.juego.modelo.ModeloJuego;
import com.juego.vista.VistaPrincipal;

public class Aplicacion {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModeloJuego modelo = new ModeloJuego();
            VistaPrincipal vista = new VistaPrincipal(modelo.getIntentosMaximos(), modelo.getLargoPalabra());
            new ControladorJuego(modelo, vista);
            vista.setVisible(true);
        });
    }
}
