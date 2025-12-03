package profit.springrest.dto.movie;

import profit.springrest.data.Genres;
import profit.springrest.dto.director.DirectorResponseDto;

public record MovieResponseDto(
        Long id,
        String title,
        int releaseYear,
        Genres genre,
        DirectorResponseDto director
) {
}
