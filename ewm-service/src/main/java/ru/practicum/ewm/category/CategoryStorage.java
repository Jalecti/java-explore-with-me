package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryStorage extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
