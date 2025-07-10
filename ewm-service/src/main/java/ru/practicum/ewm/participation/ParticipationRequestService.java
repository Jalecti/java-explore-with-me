package ru.practicum.ewm.participation;

import java.util.Collection;

public interface ParticipationRequestService {
    ParticipationRequestDto save(Long userId, Long eventId);

    Collection<ParticipationRequestDto> findByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Collection<ParticipationRequestDto> findPartRequestsByUserId(Long userId, Long eventId);

    ParticipationRequestStatusUpdateResult updatePartRequestsByInitiatorId(Long userId, Long eventId,
                                                                           ParticipationRequestStatusUpdateRequest request);
}
