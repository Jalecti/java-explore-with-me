package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.NewEventRequest;
import ru.practicum.ewm.event.UpdateEventRequest;
import ru.practicum.ewm.participation.ParticipationRequestService;
import ru.practicum.ewm.participation.ParticipationRequestStatusUpdateRequest;


@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    @PostMapping
    public ResponseEntity<Object> create(@PathVariable Long userId,
                                         @Valid @RequestBody NewEventRequest newEventRequest) {
        log.info("Creating event {} by userId:{}", newEventRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(userId, newEventRequest));
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@PathVariable Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllByInitiatorId(userId, pageable));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> findByIdAndUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findByIdAndInitiatorId(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody @Valid UpdateEventRequest request) {
        log.info("Updating event with ID:{}, body:{} by userId:{}", eventId, request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updatePrivate(userId, eventId, request));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<Object> findPartRequestsByInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestService.findPartRequestsByUserId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<Object> updatePartRequestsByInitiatorId(@PathVariable Long userId, @PathVariable Long eventId,
                                                                  @RequestBody ParticipationRequestStatusUpdateRequest request) {
        log.info("Updating participation requests {} by userId:{} in eventId:{}", request, userId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestService.updatePartRequestsByInitiatorId(userId, eventId, request));
    }
}
