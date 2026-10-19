package com.coopera2075.cooperanki.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

// Configuración de los scrapers de convocatorias (prefijo "scraper"). Todo tiene valor por defecto,
// así que no hace falta declarar nada en application.properties para usarlos
@ConfigurationProperties (prefix = "scraper")
public record ScraperProperties(
        // Con qué nombre se identifican las peticiones ante los portales consultados
        @DefaultValue ("CooperAnkiBot/0.1") String userAgent,
        @DefaultValue ("10s") Duration timeoutConexion,
        @DefaultValue ("30s") Duration timeoutLectura,
        // El listado de la AECID ocupa unos 600 KB y WebClient solo admite 256 KB por defecto
        @DefaultValue ("5MB") DataSize tamanoMaximoRespuesta,
        // Espera entre dos peticiones seguidas a la misma fuente, para no saturarla
        @DefaultValue ("1s") Duration pausaEntrePeticiones,
        @DefaultValue Aecid aecid) {

    public record Aecid(
            @DefaultValue ("https://www.aecid.gob.es/activos") String urlListado,
            // Familias del listado de procedimientos que se extraen; las demás (becas, empleo, registro de ONGD...)
            // no son convocatorias de financiación. Se comparan sin distinguir mayúsculas ni tildes
            @DefaultValue ("Subvenciones para actuaciones de cooperación para el desarrollo") List<String> familias) {
    }
}
