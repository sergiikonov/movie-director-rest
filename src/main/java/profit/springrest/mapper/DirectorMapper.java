package profit.springrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import profit.springrest.config.MapStructConfig;
import profit.springrest.data.Director;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;

import java.util.List;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface DirectorMapper {
    DirectorResponseDto toDto(Director director);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movies", ignore = true)
    Director toEntity(DirectorRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movies", ignore = true)
    void updateEntityFromDto(DirectorRequestDto dto, @MappingTarget Director director);

    List<DirectorResponseDto> toDtoList(List<Director> directors);
}

