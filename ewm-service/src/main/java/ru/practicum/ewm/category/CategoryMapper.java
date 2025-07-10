package ru.practicum.ewm.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static Category mapToCategory(NewCategoryRequest newCategoryRequest) {
        Category category = new Category();

        category.setName(newCategoryRequest.getName());
        return category;
    }

    public static Category updateCategoryFields(Category category, UpdateCategoryRequest request) {
        category.setName(request.getName());
        return category;
    }
}
