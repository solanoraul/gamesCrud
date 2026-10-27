package com.catalogo.model;

public final class Opcoes {

    public static final String STATUS_ZERADO = "Zerado";
    public static final String FILTRO_TODOS = "Todos";

    public static final String[] PLATAFORMAS = {
            "PC",
            "PlayStation",
            "Xbox",
            "Nintendo",
            "Mobile"
    };

    public static final String[] GENEROS = {
            "Ação",
            "Aventura",
            "RPG",
            "Esporte",
            "Estratégia",
            "Terror",
            "Corrida",
            "Simulação"
    };

    public static final String[] STATUS = {"Quero jogar", "Jogando", "Zerado"};

    private Opcoes() {
    }

    public static String[] filtrosStatus() {
        String[] filtros = new String[STATUS.length + 1];
        filtros[0] = FILTRO_TODOS;
        System.arraycopy(STATUS, 0, filtros, 1, STATUS.length);
        return filtros;
    }
}
