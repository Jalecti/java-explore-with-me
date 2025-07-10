package ru.practicum.ewm.compilation;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompilationStorage extends JpaRepository<Compilation, Long> {

    @EntityGraph(value = "Compilation.forMapping")
    @NonNull
    @Override
    Optional<Compilation> findById(@NonNull Long compId);

    @EntityGraph(value = "Compilation.forMapping")
    @Query("SELECT c FROM Compilation c WHERE (?1 IS NULL OR c.pinned = ?1)")
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);

}
