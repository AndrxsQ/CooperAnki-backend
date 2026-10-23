// package com.coopera2075.cooperanki.config;

// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.boot.http.client.HttpClientSettings;
// import org.springframework.boot.http.client.HttpRedirects;
// import org.springframework.boot.http.client.reactive.ClientHttpConnectorBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpHeaders;
// import org.springframework.web.reactive.function.client.WebClient;

// @Configuration
// @EnableConfigurationProperties (ScraperProperties.class)
// public class ScraperConfig {
//     // WebClient que comparten los scrapers: sigue redirecciones, corta las peticiones que no responden
//     // y admite páginas más grandes que el límite por defecto de WebClient
//     @Bean
//     public WebClient scraperWebClient(WebClient.Builder builder, ScraperProperties properties) {
//         HttpClientSettings settings = HttpClientSettings.defaults()
//                 .withTimeouts(properties.timeoutConexion(), properties.timeoutLectura())
//                 .withRedirects(HttpRedirects.FOLLOW);
//         int tamanoMaximo = Math.toIntExact(properties.tamanoMaximoRespuesta().toBytes());

//         return builder
//                 .clientConnector(ClientHttpConnectorBuilder.reactor().build(settings))
//                 .defaultHeader(HttpHeaders.USER_AGENT, properties.userAgent())
//                 .defaultHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,*/*;q=0.8")
//                 .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9,en;q=0.8")
//                 .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(tamanoMaximo))
//                 .build();
//     }
// }
