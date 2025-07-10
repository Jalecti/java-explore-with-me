package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.exception.ConflictLinkedEventsCategoryException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryUtilService {
    private final CategoryStorage categoryStorage;
    private final CategoryService categoryService;
    private final EventService eventService;

    @Transactional
    public void delete(Long categoryId) {
        Category categoryToDelete = categoryService.checkCategory(categoryId);
        checkLinkedEventsByCatId(categoryId);
        String name = categoryToDelete.getName();
        categoryStorage.deleteById(categoryId);
        log.info("Category {} with ID:{} has been deleted", name, categoryId);
    }

    private void checkLinkedEventsByCatId(Long catId) {
        if (eventService.findFirstByCategoryId(catId).isPresent()) {
            log.error("A category should not have events linked to it. CatId={}", catId);
            throw new ConflictLinkedEventsCategoryException("A category should not have events linked to it. CatId=" + catId);
        }
    }
}

