package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public void saveHit(NewEndpointHitRequest request) {
        EndpointHit endpointHit = EndpointHitMapper.mapToEndpointHit(request);
        endpointHitRepository.save(endpointHit);
        log.info("Сохранено обращение к эндпоинту: id={}, app={}, uri={}, ip={}, timestamp={}",
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp());
    }

    @Override
    public List<ViewStatsDto> getStatsWithParams(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return unique
                ? endpointHitRepository.getStatsUnique(start, end, uris)
                : endpointHitRepository.getStatsAll(start,end, uris);
    }
}
