package ru.practicum.ewm.compilation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationStorage compilationStorage;
    private final EventService eventService;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationRequest request) {
        List<Event> events = request.getEvents() == null
                ? null
                : eventService.checkEventInIdList(request.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilation(request, events);
        compilation = compilationStorage.save(compilation);
        log.info("Compilation {} is registered with the ID: {}", compilation.getTitle(), compilation.getId());
        Collection<EventShortDto> eventsShorts = events == null
                ? null
                : eventService.getMappedCollectionEventShortDto(events.stream());
        return CompilationMapper.mapToDto(compilation, eventsShorts);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        Compilation compilationToDelete = checkCompilation(compId);
        String title = compilationToDelete.getTitle();
        compilationStorage.deleteById(compId);
        log.info("Compilation {} with the ID: {} has been deleted", title, compId);
    }

    @Override
    public Collection<CompilationDto> findCompilations(Boolean pinned, Pageable pageable) {
        return compilationStorage.findByPinned(pinned, pageable)
                .stream()
                .map(compilation -> {
                    Collection<EventShortDto> shortDtos = eventService.getMappedCollectionEventShortDto(compilation.getEvents()
                            .stream());
                    return CompilationMapper.mapToDto(compilation, shortDtos);
                })
                .toList();
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = checkCompilation(compId);
        Collection<EventShortDto> shortDtos = eventService.getMappedCollectionEventShortDto(compilation.getEvents()
                .stream());
        return CompilationMapper.mapToDto(compilation, shortDtos);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest request) {
        Compilation compilationToUpdate = checkCompilation(compId);
        List<Event> events = request.hasEvents()
                ? eventService.checkEventInIdList(request.getEvents())
                : null;
        Compilation updatedCompilation = CompilationMapper.updateCompilationFields(compilationToUpdate, request, events);
        updatedCompilation = compilationStorage.save(updatedCompilation);
        log.info("Compilation has been updated with ID:{}", compId);

        Collection<EventShortDto> shortDtos = events == null
                ? null
                : eventService.getMappedCollectionEventShortDto(events.stream());
        return CompilationMapper.mapToDto(updatedCompilation, shortDtos);
    }

    private Compilation checkCompilation(Long compId) {
        return compilationStorage.findById(compId).orElseThrow(() -> {
            log.error("Compilation with ID:{} was not found", compId);
            return new NotFoundException("Compilation with ID:" + compId + " was not found");
        });
    }
}
