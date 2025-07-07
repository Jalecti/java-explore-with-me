package ru.practicum.ewm.participation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
public class ParticipationRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ParticipationRequestStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long requester;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long event;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;
}
