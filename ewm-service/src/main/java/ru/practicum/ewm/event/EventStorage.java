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
import java.util.List;
import java.util.Optional;


public interface EventStorage extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.forMapping")
    @NonNull
    @Override
    Optional<Event> findById(@NonNull Long eventId);

    @EntityGraph(value = "Event.forMapping")
    @NonNull
    @Override
    List<Event> findAllById(@NonNull Iterable<Long> eventIds);

    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @EntityGraph(value = "Event.forMapping")
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @EntityGraph(value = "Event.forMapping")
    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(Collection<Long> users, Collection<EventState> states,
                           Collection<Long> categories, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Pageable pageable);

    @EntityGraph(value = "Event.forMapping")
    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' " +
            "AND (?1 IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR e.category.id IN ?2) " +
            "AND (?3 IS NULL OR e.paid = ?3) " +
            "AND (CAST(?4 AS timestamp) IS NULL OR e.eventDate >= ?4) " +
            "AND (CAST(?5 AS timestamp) IS NULL OR e.eventDate <= ?5) " +
            "AND (?6 = false OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    @EntityGraph(value = "Event.forMapping")
    Page<Event> findPublishedByParams(String text, Collection<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

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

    @Modifying
    @Query("UPDATE Event as e " +
            "SET e.confirmedRequests = e.confirmedRequests + ?2 " +
            "WHERE e.id = ?1")
    void addConfirmedRequests(Long eventId, Long confirmedRequests);
}
