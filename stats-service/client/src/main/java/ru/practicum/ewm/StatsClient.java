package ru.practicum.ewm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-service.url}") String baseUrl,
                       RestTemplateBuilder builder) {
        super(builder
                .rootUri(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build());
    }

    public void saveHit(NewEndpointHitRequest hit) {
        post("/hit", hit);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        URI uri = builder.build(true).toUri(); // true → кодировать параметры

        ResponseEntity<Object> response = rest.exchange(uri, HttpMethod.GET, null, Object.class);
        Object body = response.getBody();
        return convertToList(body);
    }

    private List<ViewStatsDto> convertToList(Object body) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(body, new TypeReference<>() {});
    }
}
