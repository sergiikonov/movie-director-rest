package profit.springrest.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profit.springrest.data.Director;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;
import profit.springrest.exception.EntityNotFoundException;
import profit.springrest.mapper.DirectorMapper;
import profit.springrest.repository.DirectorRepository;
import profit.springrest.service.director.DirectorServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectorServiceImplTest {
    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private DirectorMapper directorMapper;

    @InjectMocks
    private DirectorServiceImpl directorService;

    @Test
    @DisplayName("Create Director: Should save and return DTO when name is unique")
    void createDirector_Success() {
        DirectorRequestDto request = new DirectorRequestDto("Tarantino");
        Director directorEntity = new Director();
        directorEntity.setName("Tarantino");

        Director savedDirector = new Director();
        savedDirector.setId(1L);
        savedDirector.setName("Tarantino");

        DirectorResponseDto expectedResponse = new DirectorResponseDto(1L, "Tarantino");

        when(directorRepository.existsByName("Tarantino")).thenReturn(false); // Name is unique
        when(directorMapper.toEntity(request)).thenReturn(directorEntity);
        when(directorRepository.save(directorEntity)).thenReturn(savedDirector);
        when(directorMapper.toDto(savedDirector)).thenReturn(expectedResponse);

        DirectorResponseDto result = directorService.createDirector(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(directorRepository).save(any(Director.class));
    }

    @Test
    @DisplayName("Update Director: Should update fields when ID exists")
    void updateDirector_Success() {
        Long id = 1L;
        DirectorRequestDto request = new DirectorRequestDto("Nolan Updated");

        Director existingDirector = new Director();
        existingDirector.setId(id);
        existingDirector.setName("Nolan Old");

        Director savedDirector = new Director();
        savedDirector.setId(id);
        savedDirector.setName("Nolan Updated");

        DirectorResponseDto expectedResponse = new DirectorResponseDto(id, "Nolan Updated");

        when(directorRepository.findById(id)).thenReturn(Optional.of(existingDirector));
        when(directorRepository.existsByName("Nolan Updated")).thenReturn(false);
        when(directorRepository.save(any(Director.class))).thenReturn(savedDirector);
        when(directorMapper.toDto(savedDirector)).thenReturn(expectedResponse);

        DirectorResponseDto result = directorService.updateDirector(request, id);

        assertEquals("Nolan Updated", result.name());
        verify(directorMapper).updateEntityFromDto(request, existingDirector);
    }

    @Test
    @DisplayName("Delete Director: Should call deleteById when ID exists")
    void deleteDirector_Success() {
        Long id = 1L;
        when(directorRepository.existsById(id)).thenReturn(true);

        directorService.deleteDirectorById(id);

        verify(directorRepository).deleteById(id);
    }

    @Test
    @DisplayName("Create Director: Should throw exception when name already exists")
    void createDirector_DuplicateName_ThrowsException() {
        DirectorRequestDto request = new DirectorRequestDto("Tarantino");
        when(directorRepository.existsByName("Tarantino")).thenReturn(true); // Name ALREADY exists

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> directorService.createDirector(request));

        assertEquals("Director with name Tarantino already exists", ex.getMessage());
        verify(directorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Director: Should throw exception when ID not found")
    void updateDirector_NotFound_ThrowsException() {
        Long id = 99L;
        DirectorRequestDto request = new DirectorRequestDto("Any Name");
        when(directorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> directorService.updateDirector(request, id));
    }
}
