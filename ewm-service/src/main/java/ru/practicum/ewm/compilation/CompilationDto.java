package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
public class CompilationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean pinned;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String title;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<EventShortDto> events;
}
