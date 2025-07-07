package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Object> findByParams(@RequestParam Collection<Long> users,
                                               @RequestParam Collection<EventState> states,
                                               @RequestParam Collection<Long> categories,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.findByParams(users, states, categories, rangeStart, rangeEnd, pageable));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> update(@PathVariable Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequest request) {
        log.info("Updating event with ID:{}, body:{}", eventId, request);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateAdmin(eventId, request));
    }
}
