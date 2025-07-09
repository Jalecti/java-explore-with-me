package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;
import java.util.Collection;

public interface CompilationService {

    CompilationDto save(NewCompilationRequest request);

    void delete(Long compId);

    Collection<CompilationDto> findCompilations(Boolean pinned, Pageable pageable);

    CompilationDto findById(Long compId);
}
