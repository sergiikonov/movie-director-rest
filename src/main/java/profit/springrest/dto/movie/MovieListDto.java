package profit.springrest.dto.movie;

import profit.springrest.data.Genres;

public record MovieListDto(
        Long id,
        String title,
        int releaseYear,
        Genres genre,
        String directorName
) {
}
