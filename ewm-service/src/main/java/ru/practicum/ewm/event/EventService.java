package ru.practicum.ewm.event;


import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventDto save(Long userId, NewEventRequest newEventRequest);

    Collection<EventShortDto> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    EventDto findByIdAndInitiatorId(Long initiatorId, Long eventId);

    EventDto findPublishedById(Long eventId);

    Collection<EventShortDto> findPublishedByParams(String text, Collection<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    Collection<EventDto> findByParams(Collection<Long> users, Collection<EventState> states, Collection<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    EventDto updateAdmin(Long eventId, UpdateEventRequest request);

    EventDto updatePrivate(Long userId, Long eventId, UpdateEventRequest request);

    Event checkEvent(Long eventId);

    List<Event> checkEventInIdList(List<Long> eventIds);

    void incrementConfirmedRequests(Long eventId);

    void decrementConfirmedRequests(Long eventId);

    void addConfirmedRequests(Long eventId, Long confirmedRequests);
}
