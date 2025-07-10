package ru.practicum.ewm.category;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface CategoryService {
    CategoryDto save(NewCategoryRequest newCategoryRequest);

    Collection<CategoryDto> findAll(Pageable pageable);

    CategoryDto findCategoryById(Long categoryId);

    CategoryDto update(Long categoryId, UpdateCategoryRequest updateCategoryRequest);

    Category checkCategory(Long categoryId);

    List<Category> checkCategoryInIdList(Collection<Long> catIds);
}
