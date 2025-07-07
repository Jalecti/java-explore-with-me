package ru.practicum.ewm.category;

import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface CategoryService {
    CategoryDto save(NewCategoryRequest newCategoryRequest);

    Collection<CategoryDto> findAll(Pageable pageable);

    CategoryDto findCategoryById(Long categoryId);

    CategoryDto update(Long categoryId, UpdateCategoryRequest updateCategoryRequest);

    void delete(Long categoryId);

    Category checkCategory(Long categoryId);
}
