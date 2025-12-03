package profit.springrest.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import profit.springrest.dto.movie.*;
import profit.springrest.service.movie.MovieService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public List<MovieListDto> findAllMovies() {
        return movieService.findAllMovies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull MovieResponseDto> findMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }

    @PostMapping
    public ResponseEntity<@NonNull MovieResponseDto> createMovie(@RequestBody @Valid MovieRequestDto dto) {
        return ResponseEntity.ok(movieService.createMovie(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull MovieResponseDto> updateMovie(@PathVariable Long id,
                                                        @RequestBody @Valid MovieRequestDto dto) {
        return ResponseEntity.ok(movieService.updateMovie(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/_list")
    public PageResponse<@NonNull MovieListDto> getFilteredMovies(@RequestBody MovieFilterRequest filter) {
        return movieService.searchMovies(filter);
    }

    @PostMapping("/_report")
    public ResponseEntity<byte[]> generateReport(@RequestBody MovieFilterRequest filter) {
        byte[] content = movieService.generateCsvReport(filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies_report.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @PostMapping("/upload")
    public ResponseEntity<@NonNull ImportResultDto> uploadMovies(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(movieService.importMovies(file));
    }
}
