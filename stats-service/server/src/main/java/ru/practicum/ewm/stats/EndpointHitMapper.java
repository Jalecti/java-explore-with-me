package ru.practicum.ewm.stats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.NewEndpointHitRequest;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointHitMapper {
    public static EndpointHit mapToEndpointHit(NewEndpointHitRequest request) {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setApp(request.getApp());
        endpointHit.setUri(request.getUri());
        endpointHit.setIp(request.getIp());
        endpointHit.setTimestamp(LocalDateTime.parse(request.getTimestamp(), StatsServiceImpl.FORMATTER));

        return endpointHit;
    }
}
