package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictCategoryNameException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryStorage categoryStorage;

    @Transactional
    @Override
    public CategoryDto save(NewCategoryRequest newCategoryRequest) {
        checkCategoryName(newCategoryRequest.getName());
        Category category = CategoryMapper.mapToCategory(newCategoryRequest);
        category = categoryStorage.save(category);
        log.info("Category {} has been created with ID: {}", category.getName(), category.getId());
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public Collection<CategoryDto> findAll(Pageable pageable) {
        return categoryStorage.findAll(pageable)
                .stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoryById(Long categoryId) {
        return CategoryMapper.mapToCategoryDto(checkCategory(categoryId));
    }

    @Transactional
    @Override
    public CategoryDto update(Long categoryId, UpdateCategoryRequest updateCategoryRequest) {
        Category categoryToUpdate = checkCategory(categoryId);
        if (!categoryToUpdate.getName().equals(updateCategoryRequest.getName())) checkCategoryName(updateCategoryRequest.getName());
        Category updatedCategory = CategoryMapper.updateCategoryFields(categoryToUpdate, updateCategoryRequest);
        updatedCategory = categoryStorage.save(updatedCategory);
        log.info("Category with ID:{} has been updated", categoryId);
        return CategoryMapper.mapToCategoryDto(updatedCategory);
    }

    @Override
    public Category checkCategory(Long categoryId) {
        return categoryStorage.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new NotFoundException("Category not found with ID: " + categoryId);
        });
    }

    private void checkCategoryName(String name) {
        if (categoryStorage.findByName(name).isPresent()) {
            log.error("Category with the specified name has already been registered: {}", name);
            throw new ConflictCategoryNameException("Category with the specified name has already been registered: " + name);
        }
    }

    @Override
    public List<Category> checkCategoryInIdList(Collection<Long> catIds) {
        List<Category> cats = categoryStorage.findAllById(catIds);
        Set<Long> foundedIds = cats.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        List<Long> notFoundedIds = catIds.stream()
                .filter(id -> !foundedIds.contains(id))
                .toList();

        if (!notFoundedIds.isEmpty()) {
            log.error("No categories with ID found: {}", notFoundedIds);
            throw new NotFoundException("No categories with ID found: " + notFoundedIds);
        }
        return cats;
    }
}
