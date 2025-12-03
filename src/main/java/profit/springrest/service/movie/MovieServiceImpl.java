package profit.springrest.service.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import profit.springrest.data.Movie;
import profit.springrest.dto.movie.*;
import profit.springrest.exception.EntityNotFoundException;
import profit.springrest.mapper.MovieMapper;
import profit.springrest.repository.DirectorRepository;
import profit.springrest.repository.MovieRepository;
import profit.springrest.repository.MovieSpecification;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final MovieMapper movieMapper;
    private final ObjectMapper objectMapper;

    @Override
    public MovieResponseDto createMovie(MovieRequestDto dto) {
        var director = directorRepository.findById(dto.directorId())
                .orElseThrow(() -> new EntityNotFoundException("Director with id: "
                        + dto.directorId() + " not found"));
        var movie = movieMapper.toEntity(dto);
        movie.setDirector(director);
        return movieMapper.toDto(movieRepository.save(movie));
    }

    @Override
    public MovieResponseDto updateMovie(Long id, MovieRequestDto dto) {
        var movie = movieRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Movie with id: " + id + " not found"));
        if (!movie.getDirector().getId().equals(dto.directorId())) {
            var newDirector = directorRepository.findById(dto.directorId())
                    .orElseThrow(() -> new EntityNotFoundException("Director with id: "
                            + dto.directorId() + " not found"));
            movie.setDirector(newDirector);
        }

        movieMapper.updateEntityFromDto(dto, movie);
        return movieMapper.toDto(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie with id: " + id + " not found");
        }
        movieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public MovieResponseDto findMovieById(Long id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Movie with id: %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieListDto> findAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MovieListDto> searchMovies(MovieFilterRequest filter) {
        Specification<Movie> spec = Specification.where(MovieSpecification.hasGenre(filter.genre()))
                .and(MovieSpecification.hasReleaseYear(filter.releaseYear()))
                .and(MovieSpecification.hasDirectorId(filter.directorId()));
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null ? filter.size() : 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> pageResult = movieRepository.findAll(spec, pageable);

        List<MovieListDto> content = pageResult.getContent().stream()
                .map(movieMapper::toListDto)
                .toList();

        return PageResponse.<MovieListDto>builder()
                .list(content)
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCsvReport(MovieFilterRequest filter) {
        Specification<Movie> spec = Specification.where(MovieSpecification.hasGenre(filter.genre()))
                .and(MovieSpecification.hasReleaseYear(filter.releaseYear()))
                .and(MovieSpecification.hasDirectorId(filter.directorId()));

        List<Movie> movies = movieRepository.findAll(spec);
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Title,Year,Genre,Director\n");

        for (Movie movie : movies) {
            csvBuilder.append(movie.getId()).append(",")
                    .append("\"").append(movie.getTitle()
                            .replace("\"", "\"\"")).append("\",")
                    .append(movie.getReleaseYear()).append(",")
                    .append(movie.getGenre()).append(",")
                    .append("\"").append(movie.getDirector().getName()).append("\"\n");
        }

        return csvBuilder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public ImportResultDto importMovies(MultipartFile file) {
        int success = 0;
        int failed = 0;

        try {
            List<MovieRequestDto> dtos = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {});

            for (MovieRequestDto dto : dtos) {
                try {
                    createMovie(dto);
                    success++;
                } catch (Exception e) {
                    failed++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON file", e);
        }

        return ImportResultDto.builder()
                .successCount(success)
                .failedCount(failed)
                .build();
    }
}
