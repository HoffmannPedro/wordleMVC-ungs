package com.juego.vista;

import com.juego.modelo.enums.EstadoLetra;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class VistaPrincipal extends JFrame {

    // =========================================================================
    // CONSTANTES DE DISEÑO (PALETA MODO OSCURO MODERNO)
    // =========================================================================

    // Colores de estado (tonos desaturados para modo oscuro)
    private static final Color COLOR_CORRECTA = Color.decode("#538D4E"); // Verde
    private static final Color COLOR_PRESENTE = Color.decode("#B59F3B"); // Amarillo
    private static final Color COLOR_AUSENTE  = Color.decode("#3A3A3C"); // Gris Oscuro

    // Colores de UI Base
    private static final Color BG_COLOR_BASE      = Color.decode("#121213"); // Fondo casi negro
    private static final Color BG_COLOR_PANEL     = Color.decode("#1A1A1B"); // Fondo secundario paneles
    private static final Color TEXT_COLOR_MAIN    = Color.decode("#FFFFFF"); // Blanco puro
    private static final Color TEXT_COLOR_SEC     = Color.decode("#818384"); // Gris texto secundario

    // Colores de Componentes
    private static final Color COLOR_TECLA_BASE    = Color.decode("#818384"); // Gris teclas teclado
    private static final Color COLOR_BORDE_CELDA   = Color.decode("#3A3A3C"); // Borde celdas vacías
    private static final Color COLOR_UI_COMPONENT  = Color.decode("#2A2A2B"); // Fondo botones/inputs
    private static final Color COLOR_UI_BORDER     = Color.decode("#565758"); // Borde botones/inputs

    // Fuentes
    private static final Font FONT_TABLERO = new Font("SansSerif", Font.BOLD, 30);
    private static final Font FONT_BOTONES = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_TECLADO = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_LABEL   = new Font("SansSerif", Font.PLAIN, 14);

    // =========================================================================
    // COMPONENTES UI
    // =========================================================================
    private final JTextField txtIntento;
    private final JButton btnProbar;
    private final JButton btnNuevoJuego;
    private final JButton btnPista;
    private final JLabel lblEstado;
    private final JLabel lblPalabra;
    private final JLabel lblDificultad;
    private final JLabel lblIdioma;
    private final JLabel lblPistasRestantes;
    private final JComboBox<String> cmbDificultad;
    private final JComboBox<String> cmbIdioma;
    private final JLabel[][] tablero;
    private final Map<Character, JLabel> teclas;

    // --- CONSTRUCTOR ---
    public VistaPrincipal(int filas, int columnas) {
        configurarVentana();

        // 1. Inicialización de componentes final
        tablero = new JLabel[filas][columnas];
        teclas = new HashMap<>();

        // Estilizar Componentes Individuales
        txtIntento = estilarTextField(new JTextField(5));
        btnProbar = estilarBotonPrincipal(new JButton("Probar"));
        btnNuevoJuego = estilarBotonSecundario(new JButton("Nuevo juego"));
        btnPista = estilarBotonSecundario(new JButton("Pista"));

        lblPalabra = estilarLabelPrincipal(new JLabel("Palabra:"));
        lblDificultad = estilarLabelSecundario(new JLabel("Dificultad:"));
        lblIdioma = estilarLabelSecundario(new JLabel("Idioma:"));
        lblPistasRestantes = estilarLabelPrincipal(new JLabel("Pistas: 3"));
        lblEstado = estilarLabelEstado(new JLabel("Ingresa una palabra y presiona Probar.", SwingConstants.CENTER));

        cmbDificultad = estilarComboBox(new JComboBox<>(new String[] { "Facil", "Dificil" }));
        cmbIdioma = estilarComboBox(new JComboBox<>(new String[] { "Español", "English" }));

        // 2. Ensamblado con Layouts y Paddings Modernos
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10)); // Espacio vertical entre paneles
        panelContenido.setBackground(BG_COLOR_BASE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding ventana
        setContentPane(panelContenido);

        panelContenido.add(crearPanelTablero(filas, columnas), BorderLayout.CENTER);
        panelContenido.add(crearPanelEntrada(), BorderLayout.NORTH);
        panelContenido.add(crearPanelInferior(), BorderLayout.SOUTH);

    }

    // =========================================================================
    // MÉTODOS DE CONSTRUCCIÓN (Layout y Estilo)
    // =========================================================================

    private void configurarVentana() {
        setTitle("Wordle MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        getContentPane().setBackground(BG_COLOR_BASE);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelTablero(int filas, int columnas) {
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setBackground(BG_COLOR_BASE);

        JPanel panelTablero = new JPanel(new GridLayout(filas, columnas, 8, 8));
        panelTablero.setBackground(BG_COLOR_BASE);

        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                JLabel celda = new JLabel(" ", SwingConstants.CENTER);
                celda.setFont(FONT_TABLERO);

                // OBLIGAMOS a Java a respetar que las celdas sean cuadradas siempre
                Dimension tamañoCelda = new Dimension(60, 45);
                celda.setPreferredSize(tamañoCelda);
                celda.setMinimumSize(tamañoCelda);
                celda.setMaximumSize(tamañoCelda);

                celda.setOpaque(true);
                celda.setBackground(BG_COLOR_BASE);
                celda.setForeground(TEXT_COLOR_MAIN);
                celda.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_CELDA, 2));

                tablero[fila][col] = celda;
                panelTablero.add(celda);
            }
        }
        contenedorCentrado.add(panelTablero);
        return contenedorCentrado;
    }

    private JPanel crearPanelEntrada() {
        JPanel panelEntrada = new JPanel(new GridBagLayout());
        panelEntrada.setBackground(BG_COLOR_PANEL);
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_AUSENTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        // CORRECCIÓN: NONE evita que el botón y el input se estiren hacia arriba
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Fila Superior: Juego
        gbc.gridy = 0;
        panelEntrada.add(lblPalabra, gbc);
        panelEntrada.add(txtIntento, gbc);
        panelEntrada.add(btnProbar, gbc);
        panelEntrada.add(new JLabel("  "), gbc);
        panelEntrada.add(btnNuevoJuego, gbc);
        panelEntrada.add(btnPista, gbc);
        panelEntrada.add(lblPistasRestantes, gbc);

        // Separador vertical
        gbc.insets = new Insets(0, 30, 0, 30);
        panelEntrada.add(new JSeparator(JSeparator.VERTICAL), gbc);

        // Configuración
        gbc.insets = new Insets(0, 5, 0, 5);
        panelEntrada.add(lblDificultad, gbc);
        panelEntrada.add(cmbDificultad, gbc);
        panelEntrada.add(lblIdioma, gbc);
        panelEntrada.add(cmbIdioma, gbc);

        return panelEntrada;
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(0, 15));
        panelInferior.setBackground(BG_COLOR_BASE);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // CORRECCIÓN: Envolvemos el teclado para que no se estire a los costados
        JPanel contenedorTeclado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedorTeclado.setBackground(BG_COLOR_BASE);
        contenedorTeclado.add(crearPanelTeclado());

        panelInferior.add(contenedorTeclado, BorderLayout.CENTER);
        panelInferior.add(lblEstado, BorderLayout.SOUTH);
        return panelInferior;
    }

    private JPanel crearPanelTeclado() {
        JPanel panelTeclado = new JPanel(new GridLayout(3, 1, 6, 6));
        panelTeclado.setBackground(BG_COLOR_BASE);

        JPanel fila1 = crearFilaTeclado("QWERTYUIOP");
        fila1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel fila2 = crearFilaTeclado("ASDFGHJKL");
        fila2.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Margen lateral fila media

        JPanel fila3 = crearFilaTeclado("ZXCVBNM");
        fila3.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // Margen lateral fila baja

        panelTeclado.add(fila1);
        panelTeclado.add(fila2);
        panelTeclado.add(fila3);
        return panelTeclado;
    }

    private JPanel crearFilaTeclado(String letrasFila) {
        JPanel fila = new JPanel(new GridLayout(1, letrasFila.length(), 6, 6));
        fila.setBackground(BG_COLOR_BASE);
        for (int i = 0; i < letrasFila.length(); i++) {
            char letra = letrasFila.charAt(i);
            JLabel tecla = new JLabel(String.valueOf(letra), SwingConstants.CENTER);
            tecla.setFont(FONT_TECLADO);
            tecla.setOpaque(true);
            tecla.setBackground(COLOR_TECLA_BASE);
            tecla.setForeground(TEXT_COLOR_MAIN);

            // OBLIGAMOS a que las teclas mantengan su forma
            Dimension tamañoTecla = new Dimension(45, 55);
            tecla.setPreferredSize(tamañoTecla);
            tecla.setMinimumSize(tamañoTecla);
            tecla.setMaximumSize(tamañoTecla);

            tecla.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BG_COLOR_BASE, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));

            fila.add(tecla);
            teclas.put(letra, tecla);
        }
        return fila;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ESTILIZADO DE COMPONENTES
    // =========================================================================

    private Border crearBordeComponente() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_UI_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding interno
        );
    }

    private JTextField estilarTextField(JTextField tf) {
        tf.setFont(FONT_TABLERO.deriveFont(20f));
        tf.setBackground(COLOR_UI_COMPONENT);
        tf.setForeground(TEXT_COLOR_MAIN);
        tf.setCaretColor(TEXT_COLOR_MAIN);
        tf.setBorder(crearBordeComponente());
        tf.setHorizontalAlignment(JTextField.CENTER);
        return tf;
    }

    private JButton estilarBotonBase(JButton btn) {
        btn.setFont(FONT_BOTONES);
        btn.setFocusPainted(false);
        btn.setBorder(crearBordeComponente());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton estilarBotonPrincipal(JButton btn) {
        estilarBotonBase(btn);
        btn.setBackground(COLOR_CORRECTA); // Botón Probar en Verde
        btn.setForeground(TEXT_COLOR_MAIN);
        return btn;
    }

    private JButton estilarBotonSecundario(JButton btn) {
        estilarBotonBase(btn);
        btn.setBackground(COLOR_UI_COMPONENT);
        btn.setForeground(TEXT_COLOR_MAIN);
        return btn;
    }

    private JLabel estilarLabelBase(JLabel lbl, Font font, Color color) {
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private JLabel estilarLabelPrincipal(JLabel lbl) {
        return estilarLabelBase(lbl, FONT_BOTONES, TEXT_COLOR_MAIN);
    }

    private JLabel estilarLabelSecundario(JLabel lbl) {
        return estilarLabelBase(lbl, FONT_LABEL, TEXT_COLOR_SEC);
    }

    private JLabel estilarLabelEstado(JLabel lbl) {
        lbl.setFont(FONT_BOTONES);
        lbl.setForeground(Color.decode("#FFD700")); // Dorado
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return lbl;
    }

    private JComboBox<String> estilarComboBox(JComboBox<String> cb) {
        cb.setFont(FONT_LABEL);
        cb.setBackground(COLOR_UI_COMPONENT);
        cb.setForeground(TEXT_COLOR_MAIN);
        cb.setBorder(new LineBorder(COLOR_UI_BORDER, 1));
        return cb;
    }

    // =========================================================================
    // EVENTOS Y LECTURA DE DATOS
    // =========================================================================

    public void onProbarClick(Runnable accion) {
        btnProbar.addActionListener(e -> accion.run());
    }

    public void onNuevoJuegoClick(Runnable accion) {
        btnNuevoJuego.addActionListener(e -> accion.run());
    }

    public void onPistaClick(Runnable accion) {
        btnPista.addActionListener(e -> accion.run());
    }

    public void onDificultadChange(Runnable accion) {
        cmbDificultad.addActionListener(e -> accion.run());
    }

    public void onIdiomaChange(Runnable accion) {
        cmbIdioma.addActionListener(e -> accion.run());
    }

    public String getIntentoUsuario() {
        return txtIntento.getText().trim().toUpperCase();
    }
    public void onEnterEnIntento(Runnable accion) {
        txtIntento.addActionListener(e -> accion.run());
    }

    public int getIndiceDificultad() {
        return cmbDificultad.getSelectedIndex();
    }

    public int getIndiceIdioma() {
        return cmbIdioma.getSelectedIndex();
    }

    // =========================================================================
    // ACTUALIZACIÓN DE ESTADOS UI     // =========================================================================

    public void mostrarMensajeEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    public void mostrarMensajeEmergente(String mensaje) {
        JLabel msgLabel = new JLabel(mensaje);
        msgLabel.setFont(FONT_LABEL);
        msgLabel.setForeground(Color.BLACK);
        JOptionPane.showMessageDialog(this, msgLabel, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void actualizarIndicadorPistas(String texto) {
        lblPistasRestantes.setText(texto);
    }

    public void setEstadoBotonPista(boolean habilitado) {
        btnPista.setEnabled(habilitado);
        btnPista.setBackground(habilitado ? COLOR_UI_COMPONENT : BG_COLOR_BASE);
        btnPista.setForeground(habilitado ? TEXT_COLOR_MAIN : COLOR_AUSENTE);
    }

    public void limpiarEntrada() {
        txtIntento.setText("");
        txtIntento.requestFocus();
    }

    public void limpiarTablero() {
        for (int fila = 0; fila < tablero.length; fila++) {
            for (int col = 0; col < tablero[fila].length; col++) {
                JLabel celda = tablero[fila][col];
                celda.setText(" ");
                celda.setBackground(BG_COLOR_BASE); // Reset a fondo base
                celda.setForeground(TEXT_COLOR_MAIN);
                // Recuperar borde de celda vacía
                celda.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_CELDA, 2));
            }
        }
    }

    public void reiniciarColoresTeclas() {
        for (JLabel tecla : teclas.values()) {
            tecla.setBackground(COLOR_TECLA_BASE);
            tecla.setForeground(TEXT_COLOR_MAIN);
        }
    }

    // =========================================================================
    // LÓGICA DE RENDERIZADO (Colores del Tablero y Teclado)
    // =========================================================================

    public void pintarCeldaTablero(int fila, int col, char letra, EstadoLetra estado) {
        JLabel celda = tablero[fila][col];
        celda.setText(String.valueOf(letra));
        celda.setForeground(TEXT_COLOR_MAIN);

        celda.setBorder(BorderFactory.createEmptyBorder());

        switch (estado) {
            case CORRECTA:
                celda.setBackground(COLOR_CORRECTA);
                break;
            case PRESENTE:
                celda.setBackground(COLOR_PRESENTE);
                break;
            case AUSENTE:
                celda.setBackground(COLOR_AUSENTE);
                break;
        }
    }

    public void pintarTecla(char letra, EstadoLetra estado) {
        JLabel tecla = teclas.get(letra);
        if (tecla == null) return;

        int prioridadActual = prioridadPorColor(tecla.getBackground());
        int prioridadNueva = prioridadPorEstado(estado);

        if (prioridadNueva >= prioridadActual) {
            tecla.setForeground(TEXT_COLOR_MAIN);
            switch (estado) {
                case CORRECTA:
                    tecla.setBackground(COLOR_CORRECTA);
                    break;
                case PRESENTE:
                    tecla.setBackground(COLOR_PRESENTE);
                    break;
                case AUSENTE:
                    tecla.setBackground(COLOR_AUSENTE);
                    break;
            }
        }
    }

    private int prioridadPorEstado(EstadoLetra estado) {
        if (estado == EstadoLetra.CORRECTA) return 3;
        if (estado == EstadoLetra.PRESENTE) return 2;
        return 1;
    }

    private int prioridadPorColor(Color color) {
        if (color.equals(COLOR_CORRECTA)) return 3;
        if (color.equals(COLOR_PRESENTE)) return 2;
        if (color.equals(COLOR_AUSENTE)) return 1;
        // COLOR_TECLA_BASE es 0
        return 0;
    }

    // =========================================================================
    // INTERNACIONALIZACIÓN
    // =========================================================================

    public void actualizarTextosUI(String titulo, String txtProbar, String txtNuevo, String txtPista,
                                   String txtLblPalabra, String txtLblDificultad, String txtLblIdioma) {
        setTitle(titulo);
        btnProbar.setText(txtProbar);
        btnNuevoJuego.setText(txtNuevo);
        btnPista.setText(txtPista);
        lblPalabra.setText(txtLblPalabra);
        lblDificultad.setText(txtLblDificultad);
        lblIdioma.setText(txtLblIdioma);
    }

    public void configurarCombos(String[] opcionesDif, int difSeleccionada, String[] opcionesIdioma, int idiomaSeleccionado) {

        cmbDificultad.removeAllItems();
        for (String op : opcionesDif) cmbDificultad.addItem(op);
        if (difSeleccionada >= 0) cmbDificultad.setSelectedIndex(difSeleccionada);

        cmbIdioma.removeAllItems();
        for (String op : opcionesIdioma) cmbIdioma.addItem(op);
        if (idiomaSeleccionado >= 0) cmbIdioma.setSelectedIndex(idiomaSeleccionado);
    }
}