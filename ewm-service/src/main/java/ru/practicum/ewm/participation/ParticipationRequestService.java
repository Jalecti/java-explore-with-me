package ru.practicum.ewm.participation;

import java.util.Collection;

public interface ParticipationRequestService {
    ParticipationRequestDto save(Long userId, Long eventId);

    Collection<ParticipationRequestDto> findByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
