package ru.practicum.ewm.stats;

import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(NewEndpointHitRequest request);

    List<ViewStatsDto> getStatsWithParams(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
