package profit.springrest.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DirectorRequestDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name
) {
}
