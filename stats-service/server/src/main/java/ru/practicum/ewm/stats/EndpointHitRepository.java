package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(
            "SELECT new ru.practicum.ewm.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
                    "FROM EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
                    "AND (?3 IS NULL OR h.uri IN ?3) " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(DISTINCT h.ip) DESC"
    )
    List<ViewStatsDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(
            "SELECT new ru.practicum.ewm.ViewStatsDto(h.app, h.uri, COUNT(h)) " +
                    "FROM EndpointHit as h " +
                    "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
                    "AND (?3 IS NULL OR h.uri IN ?3) " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY COUNT(h) DESC"
    )
    List<ViewStatsDto> getStatsAll(LocalDateTime start, LocalDateTime end, List<String> uris);
}
