package profit.springrest.service.movie;

import org.springframework.web.multipart.MultipartFile;
import profit.springrest.dto.movie.*;

import java.util.List;

public interface MovieService {
    MovieResponseDto createMovie(MovieRequestDto dto);

    MovieResponseDto updateMovie(Long id, MovieRequestDto dto);

    void deleteMovie(Long id);

    MovieResponseDto findMovieById(Long id);

    List<MovieListDto> findAllMovies();

    PageResponse<MovieListDto> searchMovies(MovieFilterRequest filter);

    byte[] generateCsvReport(MovieFilterRequest filter);

    ImportResultDto importMovies(MultipartFile file);
}
