package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.NewCompilationRequest;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody NewCompilationRequest request) {
        log.info("Creating compilation {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.save(request));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> delete(@PathVariable("compId") Long compId) {
        log.info("deleting a compilation with ID:{}", compId);
        compilationService.delete(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
