package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasEvents() {
        return events != null;
    }
}
