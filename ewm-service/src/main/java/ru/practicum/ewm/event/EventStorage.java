package ru.practicum.ewm.event;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @EntityGraph(value = "Event.forMapping")
    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit)")
    @EntityGraph(value = "Event.forMapping")
    Page<Event> findPublishedByParams(@Param("text") String text,
                                      @Param("categories") Collection<Long> categories,
                                      @Param("paid") Boolean paid,
                                      @Param("rangeStart") LocalDateTime rangeStart,
                                      @Param("rangeEnd") LocalDateTime rangeEnd,
                                      @Param("onlyAvailable") Boolean onlyAvailable,
                                      Pageable pageable);

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
