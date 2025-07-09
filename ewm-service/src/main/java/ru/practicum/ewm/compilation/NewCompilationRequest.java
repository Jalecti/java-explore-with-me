package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationRequest {
    @NotNull
    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;
}
