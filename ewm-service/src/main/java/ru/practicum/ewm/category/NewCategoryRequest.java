package ru.practicum.ewm.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryRequest {
    @NotBlank
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;
}