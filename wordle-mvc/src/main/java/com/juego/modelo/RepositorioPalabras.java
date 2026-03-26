package com.juego.modelo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RepositorioPalabras {
    private final List<String> solucionesEs;
    private final List<String> solucionesEn;
    private final Set<String> validasEs;
    private final Set<String> validasEn;
    private final Random aleatorio;

    public RepositorioPalabras() {
        this.aleatorio = new Random();

        this.solucionesEs = cargarLista("soluciones_es.txt");
        this.solucionesEn = cargarLista("soluciones_en.txt");

        // Cargamos los diccionarios masivos y les sumamos las soluciones para garantizar que la solución siempre sea válida
        this.validasEs = cargarSet("validas_es.txt");
        this.validasEs.addAll(this.solucionesEs);

        this.validasEn = cargarSet("validas_en.txt");
        this.validasEn.addAll(this.solucionesEn);
    }

    private List<String> cargarLista(String nombreArchivo) {
        List<String> lista = new ArrayList<>();
        leerArchivo(nombreArchivo, lista);
        if (lista.isEmpty()) lista.add("ERROR");
        return lista;
    }

    private Set<String> cargarSet(String nombreArchivo) {
        Set<String> set = new HashSet<>();
        leerArchivo(nombreArchivo, set);
        if (set.isEmpty()) set.add("ERROR");
        return set;
    }

    private void leerArchivo(String nombreArchivo, java.util.Collection<String> coleccion) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(nombreArchivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String palabraLimpia = linea.trim().toUpperCase();
                if (!palabraLimpia.isEmpty()) {
                    coleccion.add(palabraLimpia);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error crítico: No se pudo cargar el archivo " + nombreArchivo, e);
        }
    }

    public String palabraAleatoria(Idioma idioma) {
        List<String> bolsa = (idioma == Idioma.EN) ? solucionesEn : solucionesEs;
        return bolsa.get(aleatorio.nextInt(bolsa.size()));
    }

    public boolean existePalabra(String palabra, Idioma idioma) {
        Set<String> diccionario = (idioma == Idioma.EN) ? validasEn : validasEs;
        return diccionario.contains(palabra.toUpperCase());
    }
}