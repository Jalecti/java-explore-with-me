package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.EventService;

@RestController
@RequestMapping(path = "/admin/events/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentEventAdminController {
    private final EventService eventService;

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<Object> delete(@PathVariable Long commentId) {
        log.info("deleting a comment with ID:{} by admin", commentId);
        eventService.deleteComment(null, commentId, true);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
