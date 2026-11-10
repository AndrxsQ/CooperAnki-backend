package com.coopera2075.modules.matching;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.coopera2075.modules.matching.dto.EmbeddingWorkerRequest;
import com.coopera2075.modules.matching.dto.EmbeddingWorkerResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchingWorkerClient {
    private static final String API_KEY_HEADER = "X-Worker-API-Key";

    private final WebClient.Builder webClientBuilder;

    @Value("${app.matching.worker.url}")
    private String workerUrl;

    @Value("${app.matching.worker.api-key:}")
    private String apiKey;

    @Value("${app.matching.worker.timeout-ms:600000}")
    private long timeoutMillis;

    public List<EmbeddingWorkerResponse> generateEmbeddings(List<EmbeddingWorkerRequest> requests) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("MATCHING_WORKER_API_KEY no está configurada");
        }

        List<EmbeddingWorkerResponse> response = webClientBuilder
                .baseUrl(workerUrl)
                .build()
                .post()
                .uri("/embeddings")
                .header(API_KEY_HEADER, apiKey)
                .bodyValue(requests)
                .retrieve()
                .bodyToFlux(EmbeddingWorkerResponse.class)
                .collectList()
                .block(Duration.ofMillis(timeoutMillis));

        if (response == null) {
            throw new IllegalStateException("El worker no devolvió embeddings");
        }
        return response;
    }
}
