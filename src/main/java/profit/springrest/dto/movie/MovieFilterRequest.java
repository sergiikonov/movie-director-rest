package profit.springrest.dto.movie;

import profit.springrest.data.Genres;

public record MovieFilterRequest(
        Long directorId,
        Genres genre,
        Integer releaseYear,
        Integer page,
        Integer size
) {
}
