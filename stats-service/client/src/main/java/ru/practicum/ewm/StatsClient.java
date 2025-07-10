package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient extends BaseClient {
    private final String baseUrl;

    public StatsClient(@Value("${stats-service.url}") String baseUrl,
                       RestTemplateBuilder builder) {
        super(builder
                .rootUri(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build());
        this.baseUrl = baseUrl;
    }

    public void saveHit(NewEndpointHitRequest hit) {
        post("/hit", hit);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String encodedStart = URLEncoder.encode(formatter.format(start), StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(formatter.format(end), StandardCharsets.UTF_8);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseUrl + "/stats")
                .queryParam("start", encodedStart)
                .queryParam("end", encodedEnd)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        URI uri = builder.build(true).toUri();

        ResponseEntity<List<ViewStatsDto>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

}
