package com.juego.modelo;

public enum Dificultad {
    FACIL(6),
    DIFICIL(4);

    private final int intentosMaximos;

    Dificultad(int intentosMaximos) {
        this.intentosMaximos = intentosMaximos;
    }

    public int getIntentosMaximos() {
        return intentosMaximos;
    }
}
