package profit.springrest.service.director;

import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;

import java.util.List;

public interface DirectorService {
    public DirectorResponseDto createDirector(DirectorRequestDto dto);

    public DirectorResponseDto updateDirector(DirectorRequestDto dto, Long id);

    public List<DirectorResponseDto> getAllDirectors();

    public void deleteDirectorById(Long id);
}
