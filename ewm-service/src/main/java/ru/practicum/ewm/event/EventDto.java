package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
public class EventDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String annotation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CategoryDto category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserShortDto initiator;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Location location;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EventState state;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer participantLimit;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long confirmedRequests;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long views;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean paid;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean requestModeration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
}
