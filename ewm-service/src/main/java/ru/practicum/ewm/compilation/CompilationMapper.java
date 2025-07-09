package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation mapToCompilation(NewCompilationRequest request, List<Event> events) {
        Compilation compilation = new Compilation();

        compilation.setPinned(request.getPinned());
        compilation.setTitle(request.getTitle());
        compilation.setEvents(events);

        return compilation;
    }

    public static CompilationDto mapToDto(Compilation compilation, List<EventShortDto> events) {
        CompilationDto dto = new CompilationDto();

        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());
        dto.setEvents(events);

        return dto;
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest request, List<Event> events) {
        if (request.hasPinned()) compilation.setPinned(request.getPinned());
        if (request.hasTitle()) compilation.setTitle(request.getTitle());
        if (events != null) compilation.setEvents(events);

        return compilation;
    }
}
