package profit.springrest.service.director;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;
import profit.springrest.exception.EntityNotFoundException;
import profit.springrest.mapper.DirectorMapper;
import profit.springrest.repository.DirectorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    @Override
    public DirectorResponseDto createDirector(DirectorRequestDto dto) {
        if (directorRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Director with name " + dto.name() + " already exists");
        }

        var saved = directorRepository.save(directorMapper.toEntity(dto));
        return directorMapper.toDto(saved);
    }

    @Override
    public DirectorResponseDto updateDirector(DirectorRequestDto dto, Long id) {
        var director = directorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Director with id " + id + " not found"));

        if (!director.getName().equals(dto.name()) && directorRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Director with name " + dto.name() + " already exists");
        }

        directorMapper.updateEntityFromDto(dto, director);
        var saved = directorRepository.save(director);
        return directorMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DirectorResponseDto> getAllDirectors() {
        return directorMapper.toDtoList(directorRepository.findAll());
    }

    @Override
    public void deleteDirectorById(Long id) {
        if (!directorRepository.existsById(id)) {
            throw new EntityNotFoundException("Director with id " + id + " not found");
        }

        directorRepository.deleteById(id);
    }
}
