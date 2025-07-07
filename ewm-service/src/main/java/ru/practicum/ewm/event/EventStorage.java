package ru.practicum.ewm.event;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;


public interface EventStorage extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.forMapping")
    @NonNull
    @Override
    Optional<Event> findById(@NonNull Long eventId);

    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @EntityGraph(value = "Event.forMapping")
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(Collection<Long> users, Collection<EventState> states,
                           Collection<Long> categories, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Pageable pageable);

    @Modifying
    @Query("UPDATE Event as e " +
            "SET e.confirmedRequests = e.confirmedRequests + 1 " +
            "WHERE e.id = ?1")
    void incrementConfirmedRequests(Long eventId);

    @Modifying
    @Query("UPDATE Event as e " +
            "SET e.confirmedRequests = e.confirmedRequests - 1 " +
            "WHERE e.id = ?1")
    void decrementConfirmedRequests(Long eventId);
}
