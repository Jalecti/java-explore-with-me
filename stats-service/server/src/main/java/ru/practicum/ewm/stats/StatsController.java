package ru.practicum.ewm.stats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.ViewStatsDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid NewEndpointHitRequest request) {
        log.info("Saving endpointHit app={}, uri={}, ip={}, timestamp={}",
                request.getApp(),
                request.getUri(),
                request.getIp(),
                request.getTimestamp());
        statsService.saveHit(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatsWithParams(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        List<ViewStatsDto> stats = statsService.getStatsWithParams(start, end, uris, unique);
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}
