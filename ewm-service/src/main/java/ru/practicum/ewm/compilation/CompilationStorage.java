package ru.practicum.ewm.compilation;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompilationStorage extends JpaRepository<Compilation, Long> {

    @EntityGraph(value = "Compilation.forMapping")
    @NonNull
    @Override
    Optional<Compilation> findById(@NonNull Long compId);

    @EntityGraph(value = "Compilation.forMapping")
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
