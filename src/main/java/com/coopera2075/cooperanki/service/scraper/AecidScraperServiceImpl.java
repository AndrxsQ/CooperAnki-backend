// package com.coopera2075.cooperanki.service.scraper;

// import java.net.URI;
// import java.text.Normalizer;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.time.ZonedDateTime;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Locale;
// import java.util.Map;
// import java.util.Set;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.stream.Collectors;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.springframework.core.codec.CodecException;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientException;

// import com.coopera2075.cooperanki.config.ScraperProperties;
// import com.coopera2075.cooperanki.dto.ConvocatoriaExtraida;
// import com.coopera2075.cooperanki.entity.EstadoConvocatoria;
// import com.coopera2075.cooperanki.exception.ScraperException;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// // Extrae las convocatorias de subvenciones de la sede electrónica de la AECID (www.aecid.gob.es), un portal Liferay.
// // Primero lee el listado de procedimientos activos y después la ficha de cada convocatoria, donde están
// // la descripción y el plazo de solicitud. La sede no publica importes ni ODS: el importe solo figura en el
// // extracto del BOE y en la BDNS, así que monto_maximo, moneda y sectores_ods quedan vacíos
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class AecidScraperServiceImpl implements ConvocatoriaScraperService {
//     static final String FUENTE = "AECID";

//     // Los plazos de la sede están en hora peninsular española
//     static final ZoneId ZONA_SEDE = ZoneId.of("Europe/Madrid");

//     // Acto que publica la sede cuando un procedimiento abre solicitudes. Los procedimientos sin él son previsiones,
//     // reales decretos de concesión directa u otros trámites a los que no se puede postular
//     private static final String ACTO_CONVOCATORIA = "convocatoria";

//     private static final String ETIQUETA_FECHA_INICIO = normalizar("Fecha inicio periodo actuación ciudadano");
//     private static final String ETIQUETA_FECHA_CIERRE = normalizar("Fecha de cierre");

//     // Límite de la columna titulo de convocatorias (V4)
//     private static final int LONGITUD_MAXIMA_TITULO = 500;

//     // La sede identifica cada procedimiento con un nodo de Alfresco (workspace://SpacesStore/<uuid>) que viaja en la URL
//     private static final Pattern ID_PROCEDIMIENTO = Pattern.compile("SpacesStore(?:%2F|/)([0-9a-fA-F-]{36})");

//     // La sede ha mostrado las fechas de dos formas: "22/05/2026 23:59" (hora de Madrid) y, hasta 2025,
//     // el toString() de java.util.Date en GMT ("Wed Oct 01 21:59:00 GMT 2025")
//     private static final Pattern FORMATO_FECHA_HORA = Pattern.compile("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}");
//     private static final Pattern FORMATO_FECHA = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
//     private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//     private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//     private static final DateTimeFormatter FECHA_DATE_TO_STRING =
//             DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

//     private final WebClient scraperWebClient;
//     private final ScraperProperties properties;

//     // Procedimiento tal como aparece en el listado, antes de leer su ficha
//     record ProcedimientoListado(String id, String titulo, String url) {
//     }

//     @Override
//     public String getFuente() {
//         return FUENTE;
//     }

//     @Override
//     public List<ConvocatoriaExtraida> extraerConvocatorias() {
//         String urlListado = properties.aecid().urlListado();
//         List<ProcedimientoListado> procedimientos = parsearListado(descargar(urlListado), urlListado);
//         log.info("{}: {} convocatorias en el listado de {}", FUENTE, procedimientos.size(), urlListado);

//         LocalDate hoy = LocalDate.now(ZONA_SEDE);
//         List<ConvocatoriaExtraida> convocatorias = new ArrayList<>();
//         for (ProcedimientoListado procedimiento : procedimientos) {
//             pausar();
//             try {
//                 convocatorias.add(parsearDetalle(descargar(procedimiento.url()), procedimiento, hoy));
//             } catch (ScraperException e) {
//                 // Una ficha que falla no invalida las demás; se volverá a intentar en la próxima extracción
//                 log.warn("{}: se omite \"{}\": {}", FUENTE, procedimiento.titulo(), e.getMessage());
//             }
//         }
//         return convocatorias;
//     }

//     // El listado agrupa los procedimientos en familias (div.titulo-familia) y tipos; cada procedimiento es un
//     // div.container-button-sub-evoAccordion con su enlace "Acceder" y, en el panel desplegable que le sigue, los actos
//     // publicados. Se recorre en orden de documento para saber a qué familia pertenece cada procedimiento
//     List<ProcedimientoListado> parsearListado(String html, String urlListado) {
//         Document documento = Jsoup.parse(html, urlListado);
//         Set<String> familiasBuscadas = properties.aecid().familias().stream()
//                 .map(AecidScraperServiceImpl::normalizar)
//                 .collect(Collectors.toSet());

//         Set<String> familiasDelListado = new LinkedHashSet<>();
//         Set<String> idsVistos = new HashSet<>();
//         List<ProcedimientoListado> procedimientos = new ArrayList<>();
//         String familiaActual = null;

//         for (Element elemento : documento.select("div.titulo-familia, div.container-button-sub-evoAccordion")) {
//             if (elemento.hasClass("titulo-familia")) {
//                 familiaActual = normalizar(elemento.text());
//                 familiasDelListado.add(elemento.text());
//                 continue;
//             }
//             if (familiaActual == null || !familiasBuscadas.contains(familiaActual) || !tieneActoConvocatoria(elemento)) {
//                 continue;
//             }

//             Element enlace = elemento.selectFirst("span.acceder-detalle a[href]");
//             Element titulo = elemento.selectFirst(".tituloHtml");
//             Matcher id = enlace == null ? null : ID_PROCEDIMIENTO.matcher(enlace.attr("href"));
//             if (enlace == null || titulo == null || !id.find()) {
//                 log.debug("{}: procedimiento sin título o sin enlace reconocible: {}", FUENTE, elemento.text());
//                 continue;
//             }
//             String idProcedimiento = id.group(1).toLowerCase(Locale.ROOT);
//             if (idsVistos.add(idProcedimiento)) {
//                 procedimientos.add(new ProcedimientoListado(idProcedimiento, titulo.text(), normalizarUrl(enlace.absUrl("href"))));
//             }
//         }

//         // Sin familias la página no es el listado que se conoce (rediseño, página de error...): mejor fallar
//         // que devolver una lista vacía que parezca un día sin convocatorias
//         if (familiasDelListado.isEmpty()) {
//             throw new ScraperException("El listado de " + urlListado + " no tiene la estructura esperada");
//         }
//         boolean algunaFamiliaEncontrada = familiasDelListado.stream()
//                 .map(AecidScraperServiceImpl::normalizar)
//                 .anyMatch(familiasBuscadas::contains);
//         if (!algunaFamiliaEncontrada) {
//             log.warn("{}: ninguna familia del listado coincide con las configuradas {}; familias del listado: {}",
//                     FUENTE, properties.aecid().familias(), familiasDelListado);
//         }
//         return procedimientos;
//     }

//     // Los actos del procedimiento están en el panel desplegable que sigue al encabezado (h3) que lo contiene.
//     // Se compara el nombre completo para no confundir CONVOCATORIA con MODIFICACIÓN CONVOCATORIA, y se ignora
//     // el texto para lectores de pantalla (.sr-only: "Acceder a CONVOCATORIA")
//     private static boolean tieneActoConvocatoria(Element procedimiento) {
//         Element encabezado = procedimiento.parent();
//         Element panel = encabezado == null ? null : encabezado.nextElementSibling();
//         if (panel == null) {
//             return false;
//         }
//         return panel.select("div.acto-administrativo span.titulo-tramite span:not(.sr-only)").stream()
//                 .anyMatch(acto -> ACTO_CONVOCATORIA.equals(normalizar(acto.text())));
//     }

//     // La ficha tiene una cabecera (título, tipo, código BDNS, fecha del BOE) y una sección por cada acto publicado
//     // (CONVOCATORIA, LISTAS PROVISIONALES, SUBSANACIÓN...), cada una con sus propias fechas de inicio y cierre.
//     // El plazo para presentar solicitudes es el de la sección CONVOCATORIA
//     ConvocatoriaExtraida parsearDetalle(String html, ProcedimientoListado procedimiento, LocalDate hoy) {
//         Document documento = Jsoup.parse(html, procedimiento.url());
//         // "procedimento" está escrito así en la sede
//         Element ficha = documento.selectFirst("div.detalle-procedimento");
//         if (ficha == null) {
//             throw new ScraperException("La ficha " + procedimiento.url() + " no tiene la estructura esperada");
//         }

//         Element tituloFicha = ficha.selectFirst(".texto-titulo");
//         String titulo = tituloFicha != null && !tituloFicha.text().isBlank() ? tituloFicha.text() : procedimiento.titulo();

//         // Las etiquetas se reconocen por su texto ("Fecha de cierre: ...") y no por su marcado, que ha cambiado con el tiempo
//         Map<String, String> datosConvocatoria = new HashMap<>();
//         String descripcion = null;
//         String seccionActual = null;
//         for (Element elemento : ficha.select(".titulo-familia, .descripciones, div.texto")) {
//             if (elemento.hasClass("titulo-familia")) {
//                 seccionActual = normalizar(elemento.text());
//                 continue;
//             }
//             if (!ACTO_CONVOCATORIA.equals(seccionActual) || elemento.closest(".conatiner-documentos") != null) {
//                 continue;
//             }

//             if (elemento.hasClass("descripciones")) {
//                 if (descripcion == null && !elemento.text().isBlank()) {
//                     descripcion = elemento.text();
//                 }
//                 continue;
//             }
//             String texto = elemento.text();
//             int separador = texto.indexOf(':');
//             if (separador > 0) {
//                 datosConvocatoria.putIfAbsent(normalizar(texto.substring(0, separador)), texto.substring(separador + 1).trim());
//             }
//         }

//         LocalDate fechaInicio = parsearFecha(datosConvocatoria.get(ETIQUETA_FECHA_INICIO));
//         LocalDate fechaCierre = parsearFecha(datosConvocatoria.get(ETIQUETA_FECHA_CIERRE));
//         // La sede pone un candado abierto junto al título de los procedimientos con el plazo abierto
//         boolean plazoAbierto = ficha.selectFirst(".caja-titulo .lock-open") != null;

//         return ConvocatoriaExtraida.builder()
//                 .titulo(limitar(titulo, LONGITUD_MAXIMA_TITULO))
//                 .organismoDonante(FUENTE)
//                 .descripcion(descripcion)
//                 .fechaCierre(fechaCierre)
//                 .urlOrigen(procedimiento.url())
//                 .estado(calcularEstado(fechaInicio, fechaCierre, plazoAbierto, hoy))
//                 .externalId(FUENTE.toLowerCase(Locale.ROOT) + ":" + procedimiento.id())
//                 .build();
//     }

//     // PROXIMA mientras no empieza el plazo; con fecha de cierre, ABIERTA o CERRADA según el día en Madrid.
//     // Sin ninguna fecha ni candado abierto se trata como PROXIMA: es lo que muestra la ficha el día en que la
//     // convocatoria se publica en el BOE, antes de que figure su plazo
//     static EstadoConvocatoria calcularEstado(LocalDate fechaInicio, LocalDate fechaCierre, boolean plazoAbierto, LocalDate hoy) {
//         if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
//             return EstadoConvocatoria.PROXIMA;
//         }
//         if (fechaCierre != null) {
//             return hoy.isAfter(fechaCierre) ? EstadoConvocatoria.CERRADA : EstadoConvocatoria.ABIERTA;
//         }
//         return fechaInicio != null || plazoAbierto ? EstadoConvocatoria.ABIERTA : EstadoConvocatoria.PROXIMA;
//     }

//     // Fecha en la zona de la sede, o null si falta o tiene un formato que no se conoce
//     static LocalDate parsearFecha(String valor) {
//         if (valor == null || valor.isBlank()) {
//             return null;
//         }
//         String texto = valor.trim();
//         try {
//             if (FORMATO_FECHA_HORA.matcher(texto).matches()) {
//                 return LocalDateTime.parse(texto, FECHA_HORA).toLocalDate();
//             }
//             if (FORMATO_FECHA.matcher(texto).matches()) {
//                 return LocalDate.parse(texto, FECHA);
//             }
//             return ZonedDateTime.parse(texto, FECHA_DATE_TO_STRING).withZoneSameInstant(ZONA_SEDE).toLocalDate();
//         } catch (DateTimeParseException e) {
//             log.debug("{}: fecha con formato desconocido: {}", FUENTE, valor);
//             return null;
//         }
//     }

//     // HTML de la página; cualquier fallo de red o respuesta de error se traduce en ScraperException
//     String descargar(String url) {
//         try {
//             String html = scraperWebClient.get()
//                     // Con URI y no con String para que WebClient no vuelva a codificar el %3A%2F%2F del id del procedimiento
//                     .uri(URI.create(url))
//                     .retrieve()
//                     .bodyToMono(String.class)
//                     .block();
//             if (html == null || html.isBlank()) {
//                 throw new ScraperException("Respuesta vacía de " + url);
//             }
//             return html;
//         } catch (WebClientException | CodecException e) {
//             throw new ScraperException("No se pudo descargar " + url + ": " + e.getMessage(), e);
//         }
//     }

//     private void pausar() {
//         try {
//             Thread.sleep(properties.pausaEntrePeticiones());
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//             throw new ScraperException("Extracción de " + FUENTE + " interrumpida", e);
//         }
//     }

//     // La sede enlaza con el puerto explícito (https://www.aecid.gob.es:443/...); se quita, junto con el fragmento,
//     // para que la misma convocatoria tenga siempre la misma url_origen
//     static String normalizarUrl(String url) {
//         int fragmento = url.indexOf('#');
//         String sinFragmento = fragmento >= 0 ? url.substring(0, fragmento) : url;
//         return sinFragmento.replaceFirst("^(https://[^/:]+):443(?=/|$)", "$1");
//     }

//     // Minúsculas, sin tildes y con los espacios colapsados, para comparar textos de la sede
//     static String normalizar(String texto) {
//         String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
//         return sinTildes.replaceAll("[\\s\\u00a0]+", " ").trim().toLowerCase(Locale.ROOT);
//     }

//     private static String limitar(String texto, int longitudMaxima) {
//         return texto.length() <= longitudMaxima ? texto : texto.substring(0, longitudMaxima - 1).trim() + "…";
//     }
// }
