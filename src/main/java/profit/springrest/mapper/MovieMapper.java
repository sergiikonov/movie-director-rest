package profit.springrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import profit.springrest.config.MapStructConfig;
import profit.springrest.data.Movie;
import profit.springrest.dto.movie.MovieListDto;
import profit.springrest.dto.movie.MovieRequestDto;
import profit.springrest.dto.movie.MovieResponseDto;

@Mapper(componentModel = "spring",
        config = MapStructConfig.class,
        uses = {DirectorMapper.class})
public interface MovieMapper {
    MovieResponseDto toDto(Movie movie);

    @Mapping(target = "directorName", source = "director.name")
    MovieListDto toListDto(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "director", ignore = true)
    Movie toEntity(MovieRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "director", ignore = true)
    void updateEntityFromDto(MovieRequestDto dto, @MappingTarget Movie movie);
}
