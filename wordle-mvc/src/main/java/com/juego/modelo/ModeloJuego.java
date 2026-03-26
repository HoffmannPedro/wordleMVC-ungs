package com.juego.modelo;

import com.juego.modelo.enums.EstadoLetra;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Instant;
import java.time.Duration;

public class ModeloJuego {

    // --- CONSTANTES ---
    public static final int LARGO_PALABRA = 5;
    public static final int MAX_USOS_PISTA = 3;
    private static final int CANTIDAD_DESCARTE_PISTA = 3;
    private static final String ABECEDARIO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // --- DEPENDENCIAS Y CONFIGURACIÓN ---
    private final RepositorioPalabras repositorioPalabras;
    private Dificultad dificultad;
    private Idioma idioma;

    // --- ESTADO DEL JUEGO ---
    private String palabraSecreta;
    private int intentosMaximos;
    private int intentosUsados;
    private boolean ganado;
    private Instant inicioPartida;
    private Instant finPartida;
    private int pistasRestantes;
    private Set<Character> letrasDescartadasPorPista;

    // --- CONSTRUCTORES ---
    public ModeloJuego() {
        this(new RepositorioPalabras());
    }

    public ModeloJuego(RepositorioPalabras repositorioPalabras) {
        this.repositorioPalabras = repositorioPalabras;
        this.dificultad = Dificultad.FACIL;
        this.idioma = Idioma.ES;
        reiniciar();
    }

    // =========================================================================
    // LÓGICA PRINCIPAL DE NEGOCIO (Acciones)
    // =========================================================================

    public void reiniciar() {
        this.palabraSecreta = repositorioPalabras.palabraAleatoria(idioma).toUpperCase();
        this.intentosMaximos = dificultad.getIntentosMaximos();
        this.intentosUsados = 0;
        this.ganado = false;
        this.inicioPartida = Instant.now();
        this.finPartida = Instant.now();
        this.pistasRestantes = MAX_USOS_PISTA;
        this.letrasDescartadasPorPista = new HashSet<>();
    }

    public EstadoLetra[] evaluarIntento(String intento) {
        String intentoNormalizado = intento.toUpperCase();
        EstadoLetra[] resultado = new EstadoLetra[palabraSecreta.length()];
        Arrays.fill(resultado, EstadoLetra.AUSENTE);

        boolean[] usadaEnSecreta = new boolean[palabraSecreta.length()];

        // Primera pasada: letras correctas
        for (int i = 0; i < palabraSecreta.length(); i++) {
            if (intentoNormalizado.charAt(i) == palabraSecreta.charAt(i)) {
                resultado[i] = EstadoLetra.CORRECTA;
                usadaEnSecreta[i] = true;
            }
        }

        // Segunda pasada: letras presentes en posiciones incorrectas
        for (int i = 0; i < palabraSecreta.length(); i++) {
            if (resultado[i] == EstadoLetra.CORRECTA) {
                continue;
            }
            for (int j = 0; j < palabraSecreta.length(); j++) {
                if (!usadaEnSecreta[j] && intentoNormalizado.charAt(i) == palabraSecreta.charAt(j)) {
                    resultado[i] = EstadoLetra.PRESENTE;
                    usadaEnSecreta[j] = true;
                    break;
                }
            }
        }

        intentosUsados++;
        ganado = intentoNormalizado.equals(palabraSecreta);
        if (terminoPartida()) {
            this.finPartida = Instant.now();
        }
        return resultado;
    }

    public List<Character> pedirPista() {
        if (dificultad != Dificultad.FACIL || pistasRestantes <= 0 || terminoPartida()) {
            return Collections.emptyList();
        }

        List<Character> candidatas = new ArrayList<>();
        for (int i = 0; i < ABECEDARIO.length(); i++) {
            char letra = ABECEDARIO.charAt(i);
            if (palabraSecreta.indexOf(letra) == -1 && !letrasDescartadasPorPista.contains(letra)) {
                candidatas.add(letra);
            }
        }

        if (candidatas.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.shuffle(candidatas);
        int cantidad = Math.min(CANTIDAD_DESCARTE_PISTA, candidatas.size());
        List<Character> descartadasAhora = new ArrayList<>(candidatas.subList(0, cantidad));

        letrasDescartadasPorPista.addAll(descartadasAhora);
        pistasRestantes--;

        return descartadasAhora;
    }

    // =========================================================================
    // VALIDACIONES Y CONSULTAS DE ESTADO
    // =========================================================================

    public boolean esIntentoValido(String intento) {
        return repositorioPalabras.existePalabra(intento, idioma);
    }

    public boolean terminoPartida() {
        return ganado || intentosUsados >= intentosMaximos;
    }

    public boolean isGanado() {
        return ganado;
    }

    public boolean tienePistasDisponibles() {
        return pistasRestantes > 0 && dificultad == Dificultad.FACIL && !terminoPartida();
    }

    public long getSegundosTranscurridos() {
        Instant fin = terminoPartida() ? finPartida : Instant.now();
        return Duration.between(inicioPartida, fin).getSeconds();
    }

    // =========================================================================
    // GETTERS Y SETTERS BÁSICOS
    // =========================================================================

    public int getLargoPalabra() {
        return LARGO_PALABRA;
    }

    public String getPalabraSecreta() {
        return palabraSecreta;
    }

    public int getIntentosUsados() {
        return intentosUsados;
    }

    public int getIntentosMaximos() {
        return intentosMaximos;
    }

    public int getPistasRestantes() {
        return pistasRestantes;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }
}