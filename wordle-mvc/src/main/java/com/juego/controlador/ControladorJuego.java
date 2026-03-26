package com.juego.controlador;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;

import com.juego.modelo.Dificultad;
import com.juego.modelo.Idioma;
import com.juego.modelo.ModeloJuego;
import com.juego.modelo.enums.EstadoLetra;
import com.juego.vista.VistaPrincipal;

public class ControladorJuego {

    // --- ATRIBUTOS ---
    private final ModeloJuego modelo;
    private final VistaPrincipal vista;
    private Idioma idiomaActual;
    private ResourceBundle mensajes;

    // --- CONSTRUCTOR ---
    public ControladorJuego(ModeloJuego modelo, VistaPrincipal vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.idiomaActual = Idioma.ES;
        this.modelo.setIdioma(idiomaActual);
        this.mensajes = ResourceBundle.getBundle("mensajes", new Locale("es"));

        enlazarEventos();
        aplicarTextosIdioma();
        reiniciarJuego();
    }

    // =========================================================================
    // CONFIGURACIÓN INICIAL
    // =========================================================================

    private void enlazarEventos() {
        vista.onProbarClick(this::procesarIntento);
        vista.onEnterEnIntento(this::procesarIntento);
        vista.onNuevoJuegoClick(this::reiniciarJuego);
        vista.onPistaClick(this::usarPista);
        vista.onDificultadChange(this::cambiarDificultad);
        vista.onIdiomaChange(this::cambiarIdioma);
    }

    // =========================================================================
    // MANEJADORES DE EVENTOS (Acciones del Usuario)
    // =========================================================================

    private void procesarIntento() {
        String intento = vista.getIntentoUsuario();

        if (!esEntradaValida(intento) || modelo.terminoPartida()) {
            return;
        }

        EstadoLetra[] resultado = modelo.evaluarIntento(intento);
        int fila = modelo.getIntentosUsados() - 1;

        pintarFila(fila, intento, resultado);
        actualizarColoresTeclado(intento, resultado);

        if (modelo.terminoPartida()) {
            gestionarFinPartida();
        } else {
            vista.mostrarMensajeEstado(traducir("intento") + " " + modelo.getIntentosUsados() + " " + traducir("de") + " " + modelo.getIntentosMaximos());
        }

        vista.limpiarEntrada();
        actualizarEstadoBotonPista();
    }

    private void reiniciarJuego() {
        modelo.setDificultad(vista.getIndiceDificultad() == 0 ? Dificultad.FACIL : Dificultad.DIFICIL);
        modelo.reiniciar();

        vista.limpiarTablero();
        vista.reiniciarColoresTeclas();

        actualizarIndicadorPistas();
        actualizarEstadoBotonPista();

        vista.limpiarEntrada();
        vista.mostrarMensajeEstado(traducir("nuevoJuego") + " " + traducir("tenes") + " " + modelo.getIntentosMaximos() + " " + traducir("intentos") + ".");
    }

    private void usarPista() {
        if (modelo.getDificultad() != Dificultad.FACIL) {
            vista.mostrarMensajeEstado(traducir("pistaSoloFacil"));
            return;
        }
        if (modelo.getPistasRestantes() <= 0) {
            vista.mostrarMensajeEstado(traducir("pistasAgotadas"));
            vista.mostrarMensajeEmergente(traducir("pistasAgotadas"));
            actualizarEstadoBotonPista();
            return;
        }
        if (modelo.terminoPartida()) {
            vista.mostrarMensajeEstado(traducir("partidaTerminada"));
            return;
        }

        List<Character> descartadasAhora = modelo.pedirPista();

        if (descartadasAhora.isEmpty()) {
            vista.mostrarMensajeEmergente(traducir("sinPistas"));
            return;
        }

        for (char letra : descartadasAhora) {
            vista.pintarTecla(letra, EstadoLetra.AUSENTE); // 'AUSENTE' es gris/descartado
        }

        String letras = unirLetras(descartadasAhora);
        actualizarIndicadorPistas();
        actualizarEstadoBotonPista();
        vista.mostrarMensajeEmergente(traducir("pistaDescarto") + " " + letras);
        vista.mostrarMensajeEstado(traducir("pistaAplicada") + " " + letras);
    }

    private void cambiarDificultad() {
        reiniciarJuego();
    }

    private void cambiarIdioma() {
        idiomaActual = vista.getIndiceIdioma() == 0 ? Idioma.ES : Idioma.EN;
        modelo.setIdioma(idiomaActual);

        Locale locale = (idiomaActual == Idioma.ES) ? new Locale("es") : new Locale("en");
        this.mensajes = ResourceBundle.getBundle("mensajes", locale);

        aplicarTextosIdioma();
        reiniciarJuego();
        vista.mostrarMensajeEstado(traducir("idiomaCambiado") + " " + traducir("nuevoJuego") + " " + traducir("tenes") + " "
                + modelo.getIntentosMaximos() + " " + traducir("intentos") + ".");
    }

    // =========================================================================
    // LÓGICA DE FLUJO DE JUEGO (Auxiliares)
    // =========================================================================

    private boolean esEntradaValida(String intento) {
        if (intento.length() != modelo.getLargoPalabra()) {
            vista.mostrarMensajeEstado(traducir("largoPalabra") + " " + modelo.getLargoPalabra() + " " + traducir("letras"));
            return false;
        }
        if (!intento.matches("[A-Z]+")) {
            vista.mostrarMensajeEstado(traducir("soloLetras"));
            return false;
        }
        if (!modelo.esIntentoValido(intento)) {
            vista.mostrarMensajeEstado(traducir("palabraInvalida"));
            return false;
        }
        return true;
    }

    private void gestionarFinPartida() {
        if (modelo.isGanado()) {
            String mensaje = traducir("ganasteEn") + " " + modelo.getIntentosUsados() + " " + traducir("intentos") + ". "
                    + traducir("tiempo") + ": " + formatearTiempo(modelo.getSegundosTranscurridos()) + ".";
            vista.mostrarMensajeEstado(mensaje);
            vista.mostrarMensajeEmergente(traducir("popupGano") + " " + traducir("tiempo") + ": "
                    + formatearTiempo(modelo.getSegundosTranscurridos()) + ".");
        } else {
            String mensaje = traducir("perdiste") + " " + traducir("palabraEra") + " " + modelo.getPalabraSecreta() + ". "
                    + traducir("tiempo") + ": " + formatearTiempo(modelo.getSegundosTranscurridos()) + ".";
            vista.mostrarMensajeEstado(mensaje);
            vista.mostrarMensajeEmergente(traducir("juegoTerminado") + " " + traducir("palabraEra") + " "
                    + modelo.getPalabraSecreta() + ". " + traducir("tiempo") + ": "
                    + formatearTiempo(modelo.getSegundosTranscurridos()) + ".");
        }
        actualizarEstadoBotonPista();
    }

    // =========================================================================
    // ACTUALIZACIÓN DE INTERFAZ (Delegación a la Vista)
    // =========================================================================

    private void pintarFila(int fila, String intento, EstadoLetra[] resultado) {
        for (int col = 0; col < intento.length(); col++) {
            vista.pintarCeldaTablero(fila, col, intento.charAt(col), resultado[col]);
        }
    }

    private void actualizarColoresTeclado(String intento, EstadoLetra[] resultado) {
        for (int i = 0; i < intento.length(); i++) {
            vista.pintarTecla(intento.charAt(i), resultado[i]);
        }
    }

    private void actualizarIndicadorPistas() {
        if (idiomaActual == Idioma.EN) {
            if (modelo.getDificultad() == Dificultad.FACIL) {
                vista.actualizarIndicadorPistas("Hints: " + modelo.getPistasRestantes() + "/" + ModeloJuego.MAX_USOS_PISTA);
            } else {
                vista.actualizarIndicadorPistas("Hints: disabled");
            }
        } else {
            if (modelo.getDificultad() == Dificultad.FACIL) {
                vista.actualizarIndicadorPistas("Pistas: " + modelo.getPistasRestantes() + "/" + ModeloJuego.MAX_USOS_PISTA);
            } else {
                vista.actualizarIndicadorPistas("Pistas: deshabilitadas");
            }
        }
    }

    private void actualizarEstadoBotonPista() {
        vista.setEstadoBotonPista(modelo.getDificultad() == Dificultad.FACIL && !modelo.terminoPartida() && modelo.getPistasRestantes() > 0);
    }

    // =========================================================================
    // INTERNACIONALIZACIÓN Y UTILIDADES
    // =========================================================================

    private void aplicarTextosIdioma() {
        if (idiomaActual == Idioma.ES) {
            vista.actualizarTextosUI("Wordle MVC", "Probar", "Nuevo juego", "Pista", "Palabra:", "Dificultad:", "Idioma:");
            vista.configurarCombos(new String[]{"Facil", "Dificil"}, vista.getIndiceDificultad(), new String[]{"Español", "English"}, 0);
        } else {
            vista.actualizarTextosUI("Wordle MVC", "Try", "New game", "Hint", "Word:", "Difficulty:", "Language:");
            vista.configurarCombos(new String[]{"Easy", "Hard"}, vista.getIndiceDificultad(), new String[]{"Spanish", "English"}, 1);
        }
        actualizarIndicadorPistas();
        actualizarEstadoBotonPista();
    }

    private String traducir(String clave) {
        try {
            return mensajes.getString(clave);
        } catch (Exception e) {
            return clave;
        }
    }

    private String formatearTiempo(long segundos) {
        long minutos = segundos / 60;
        long restoSegundos = segundos % 60;
        return String.format("%02d:%02d", minutos, restoSegundos);
    }

    private String unirLetras(List<Character> letras) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < letras.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(letras.get(i));
        }
        return sb.toString();
    }
}