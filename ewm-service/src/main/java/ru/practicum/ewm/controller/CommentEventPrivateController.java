package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.comment.NewCommentRequest;

@RestController
@RequestMapping(path = "/users/{userId}/events/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentEventPrivateController {
    private final EventService eventService;

    @PostMapping("/{eventId}")
    public ResponseEntity<Object> comment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody NewCommentRequest request) {
        log.info("Creating comment:{} for eventId:{} by authorId:{}", request, eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.comment(userId, eventId, request));
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<Object> delete(@PathVariable Long userId,
                                         @PathVariable Long commentId) {
        log.info("deleting a comment with ID:{} by userId:{}", commentId, userId);
        eventService.deleteComment(userId, commentId, false);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
