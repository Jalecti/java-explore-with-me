package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
    public List<ViewStatsDto> getStatsWithParams(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDateTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(end, FORMATTER);
        checkDateTimes(startDateTime, endDateTime);

        return unique
                ? endpointHitRepository.getStatsUnique(startDateTime, endDateTime, uris)
                : endpointHitRepository.getStatsAll(startDateTime, endDateTime, uris);
    }

    private void checkDateTimes(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            log.error("start={} не может быть после end={}", start, end);
            throw new ValidationException("start=" + start + " не может быть после end=" + end);
        }
    }

}
