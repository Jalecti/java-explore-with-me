package ru.practicum.ewm.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestStatusUpdateRequest {
    private Collection<Long> requestIds;
    private ParticipationRequestStatus status;
}
