package ru.practicum.ewm.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class UserShortDto {
    private Long id;
    private String name;
}