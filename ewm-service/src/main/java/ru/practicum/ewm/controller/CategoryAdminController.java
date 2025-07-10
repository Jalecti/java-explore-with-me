package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.CategoryUtilService;
import ru.practicum.ewm.category.NewCategoryRequest;
import ru.practicum.ewm.category.UpdateCategoryRequest;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final CategoryUtilService categoryUtilService;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody NewCategoryRequest newCategoryRequest) {
        log.info("Creating category {}", newCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(newCategoryRequest));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> delete(@PathVariable("catId") Long catId) {
        log.info("deleting a category with ID:{}", catId);
        categoryUtilService.delete(catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> update(@PathVariable("catId") Long catId,
                                         @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        log.info("Updating category id={} body={}", catId, updateCategoryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(catId, updateCategoryRequest));
    }
}
