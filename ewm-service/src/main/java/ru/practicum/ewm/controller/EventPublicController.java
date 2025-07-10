package ru.practicum.ewm.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventSortType;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Object> findPublishedByParams(
            HttpServletRequest request,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Collection<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSortType sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {


        if (text == null || text.isBlank()) {
            text = "";
        }

        String sortBy = "id";

        if (EventSortType.EVENT_DATE.equals(sort)) sortBy = "eventDate";
        if (EventSortType.VIEWS.equals(sort)) sortBy = "views";

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sortBy));
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findPublishedByParams(
                request, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> findPublishedById(HttpServletRequest request,
                                                    @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findPublishedById(request, eventId));
    }

}
