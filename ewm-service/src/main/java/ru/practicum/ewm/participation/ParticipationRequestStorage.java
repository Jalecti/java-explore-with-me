package ru.practicum.ewm.participation;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;
import java.util.Optional;

public interface ParticipationRequestStorage extends JpaRepository<ParticipationRequest, Long> {
    @EntityGraph(value = "ParticipationRequest.forMapping")
    @NonNull
    @Override
    Optional<ParticipationRequest> findById(@NonNull Long requestId);

    @EntityGraph(value = "ParticipationRequest.forMapping")
    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long userId, Long eventId);

    @EntityGraph(value = "ParticipationRequest.forMapping")
    Collection<ParticipationRequest> findByRequesterId(Long userId);
}
