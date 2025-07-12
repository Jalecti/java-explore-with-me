package ru.practicum.ewm.event.comment;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
public class CommentDto {
    private Long id;
    private String text;
    private Long eventId;
    private String authorName;
    private LocalDateTime created;
}
