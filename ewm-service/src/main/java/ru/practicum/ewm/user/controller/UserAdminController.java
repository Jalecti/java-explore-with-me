package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.UserService;

import org.springframework.data.domain.Pageable;
import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> findUsers(@RequestParam (required = false) Collection<Long> ids,
                                            @RequestParam (required = false, defaultValue = "0") Integer from,
                                            @RequestParam (required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllByParams(ids, pageable));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Creating user {}", newUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(newUserRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("deleting a user with ID:{}", userId);
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
