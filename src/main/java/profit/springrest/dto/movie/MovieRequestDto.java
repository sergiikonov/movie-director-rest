package profit.springrest.dto.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import profit.springrest.data.Genres;

public record MovieRequestDto(
        @NotBlank(message = "Title is required")
        String title,
        @Min(value = 1888, message = "Release year must be valid (after 1888)")
        int releaseYear,
        @NotNull(message = "Genre is required")
        Genres genre,
        @NotNull(message = "Director ID is required")
        Long directorId
) {
}
