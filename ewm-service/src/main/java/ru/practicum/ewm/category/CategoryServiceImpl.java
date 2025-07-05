package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictCategoryNameException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
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
        Category updatedCategory = CategoryMapper.updateCategoryFields(categoryToUpdate, updateCategoryRequest);
        updatedCategory = categoryStorage.save(updatedCategory);
        log.info("Category with ID:{} has been updated", categoryId);
        return CategoryMapper.mapToCategoryDto(updatedCategory);
    }

    @Transactional
    @Override
    public void delete(Long categoryId) {
        Category categoryToDelete = checkCategory(categoryId);
        String name = categoryToDelete.getName();
        categoryStorage.deleteById(categoryId);
        log.info("Category {} with ID:{} has been deleted", name, categoryId);
    }

    private Category checkCategory(Long categoryId) {
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
}
