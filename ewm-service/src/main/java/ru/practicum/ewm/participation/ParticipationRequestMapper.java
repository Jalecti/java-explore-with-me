package ru.practicum.ewm.participation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationRequestMapper {
    public static ParticipationRequestDto mapToDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();

        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setStatus(request.getStatus());
        dto.setCreated(request.getCreated());

        return dto;
    }
}
