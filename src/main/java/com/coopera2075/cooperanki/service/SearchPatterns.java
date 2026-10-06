package com.coopera2075.cooperanki.service;

// Construye patrones LIKE a partir del texto que escribe el usuario en las búsquedas
final class SearchPatterns {
    // Carácter de escape que deben declarar las consultas LIKE que usen estos patrones
    static final char ESCAPE = '!';

    private SearchPatterns() {
    }

    // Patrón "contiene" en minúsculas; un texto vacío o nulo produce "%" (coincide con todo).
    // Se escapan % y _ para que se busquen como caracteres literales y no como comodines.
    static String contains(String text) {
        if (text == null || text.isBlank()) {
            return "%";
        }
        String escaped = text.trim().toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}
