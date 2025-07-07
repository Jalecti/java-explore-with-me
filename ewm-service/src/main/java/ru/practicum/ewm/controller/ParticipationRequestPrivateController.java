package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participation.ParticipationRequestService;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping
    public ResponseEntity<Object> save(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Saving participation request by userId:{} to eventId:{}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(participationRequestService.save(userId, eventId));
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestService.findByUserId(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<Object> cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Canceling participation requestId:{} by userId:{}", requestId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestService.cancelRequest(userId, requestId));
    }
}
