package profit.springrest.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import profit.springrest.data.Director;
import profit.springrest.data.Genres;
import profit.springrest.data.Movie;
import profit.springrest.dto.movie.*;
import profit.springrest.exception.EntityNotFoundException;
import profit.springrest.mapper.MovieMapper;
import profit.springrest.repository.DirectorRepository;
import profit.springrest.repository.MovieRepository;
import profit.springrest.service.movie.MovieServiceImpl;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    @DisplayName("Create Movie: Success")
    void createMovie_Success() {
        MovieRequestDto request = new MovieRequestDto("Inception", 2010, Genres.SCI_FI, 1L);
        Director director = new Director(); director.setId(1L);
        Movie movie = new Movie();
        Movie savedMovie = new Movie(); savedMovie.setId(10L);
        MovieResponseDto response = new MovieResponseDto(10L, "Inception", 2010, Genres.SCI_FI, null);

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieMapper.toEntity(request)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(savedMovie);
        when(movieMapper.toDto(savedMovie)).thenReturn(response);

        MovieResponseDto result = movieService.createMovie(request);

        assertNotNull(result);
        assertEquals(10L, result.id());
        verify(movieRepository).save(movie);
    }

    @Test
    @DisplayName("Create Movie: Director Not Found")
    void createMovie_DirectorNotFound() {
        MovieRequestDto request = new MovieRequestDto("Title", 2020, Genres.DRAMA, 99L);
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movieService.createMovie(request));
    }

    @Test
    @DisplayName("Update Movie: Success (Director changed)")
    void updateMovie_Success() {
        Long movieId = 10L;
        Long newDirectorId = 2L;
        MovieRequestDto request = new MovieRequestDto("Updated", 2021, Genres.DRAMA, newDirectorId);

        Director oldDirector = new Director(); oldDirector.setId(1L);
        Director newDirector = new Director(); newDirector.setId(newDirectorId);

        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setDirector(oldDirector);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(directorRepository.findById(newDirectorId)).thenReturn(Optional.of(newDirector));
        when(movieRepository.save(existingMovie)).thenReturn(existingMovie);
        when(movieMapper.toDto(existingMovie)).thenReturn(
                new MovieResponseDto(movieId, "Updated", 2021, Genres.DRAMA, null));

        movieService.updateMovie(movieId, request);

        verify(movieMapper).updateEntityFromDto(request, existingMovie);
        assertEquals(newDirector, existingMovie.getDirector());
    }

    @Test
    @DisplayName("Delete Movie: Success")
    void deleteMovie_Success() {
        when(movieRepository.existsById(10L)).thenReturn(true);
        movieService.deleteMovie(10L);
        verify(movieRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Search Movies: Should return PageResponse")
    void searchMovies_Success() {
        MovieFilterRequest filter = new MovieFilterRequest(null, null, null, 0, 10);
        Page<Movie> page = new PageImpl<>(List.of(new Movie()));

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(movieMapper.toListDto(any())).thenReturn(
                new MovieListDto(1L, "T", 2000, Genres.DRAMA, "D"));

        PageResponse<MovieListDto> result = movieService.searchMovies(filter);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("Generate CSV: Should format string correctly")
    void generateCsvReport_Success() {
        MovieFilterRequest filter = new MovieFilterRequest(null, null, null, 0, 10);

        Director d = new Director(); d.setName("Nolan");
        Movie m = new Movie();
        m.setId(1L); m.setTitle("Inception"); m.setReleaseYear(2010); m.setGenre(Genres.SCI_FI); m.setDirector(d);

        when(movieRepository.findAll(any(Specification.class))).thenReturn(List.of(m));

        byte[] bytes = movieService.generateCsvReport(filter);
        String csvContent = new String(bytes);

        assertTrue(csvContent.contains("ID,Title,Year,Genre,Director"));
        assertTrue(csvContent.contains("1,\"Inception\",2010,SCI_FI,\"Nolan\""));
    }

    @Test
    @DisplayName("Import: Should count success and failures")
    void importMovies_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        MovieRequestDto validDto = new MovieRequestDto("Valid", 2000, Genres.DRAMA, 1L);
        MovieRequestDto invalidDto = new MovieRequestDto("Invalid", 2000, Genres.DRAMA, 99L);
        List<MovieRequestDto> dtos = List.of(validDto, invalidDto);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(dtos);

        Director director = new Director();

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieMapper.toEntity(validDto)).thenReturn(new Movie());
        when(movieRepository.save(any())).thenReturn(new Movie());

        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        ImportResultDto result = movieService.importMovies(file);

        assertEquals(1, result.successCount());
        assertEquals(1, result.failedCount());
    }
}
