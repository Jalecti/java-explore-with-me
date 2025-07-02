package ru.practicum.ewm.stats;

import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void saveHit(NewEndpointHitRequest request);

    List<ViewStatsDto> getStatsWithParams(String start, String end, List<String> uris, Boolean unique);
}
