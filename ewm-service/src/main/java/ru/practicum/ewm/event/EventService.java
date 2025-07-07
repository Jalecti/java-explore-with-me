package ru.practicum.ewm.event;


import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;

public interface EventService {
    EventDto save(Long userId, NewEventRequest newEventRequest);

    Collection<EventShortDto> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    EventDto findByIdAndInitiatorId(Long initiatorId, Long eventId);

    Collection<EventDto> findByParams(Collection<Long> users, Collection<EventState> states, Collection<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    EventDto updateAdmin(Long eventId, UpdateEventAdminRequest request);

    Event checkEvent(Long eventId);

    void incrementConfirmedRequests(Long eventId);

    void decrementConfirmedRequests(Long eventId);
}
