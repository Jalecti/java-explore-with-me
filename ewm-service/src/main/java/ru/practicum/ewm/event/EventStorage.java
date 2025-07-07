package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;


public interface EventStorage extends JpaRepository<Event, Long> {
    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @EntityGraph(value = "Event.forMapping")
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(Collection<Long> users, Collection<EventState> states,
                           Collection<Long> categories, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Pageable pageable);
}
