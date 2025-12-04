package profit.springrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Movies", description = "Movie Management")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get all movies (non-paginated)")
    @GetMapping
    public ResponseEntity<List<MovieListDto>> findAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }

    @Operation(summary = "Get movie details by ID")
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> findMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }

    @Operation(summary = "Create a new movie")
    @ApiResponse(responseCode = "201", description = "Movie created successfully")
    @PostMapping
    public ResponseEntity<MovieResponseDto> createMovie(@RequestBody @Valid MovieRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(dto));
    }

    @Operation(summary = "Update a movie")
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDto> updateMovie(@PathVariable Long id,
                                                        @RequestBody @Valid MovieRequestDto dto) {
        return ResponseEntity.ok(movieService.updateMovie(id, dto));
    }

    @Operation(summary = "Delete a movie")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search movies with filtering and pagination")
    @PostMapping("/_list")
    public ResponseEntity<PageResponse<MovieListDto>> getFilteredMovies(@RequestBody MovieFilterRequest filter) {
        return ResponseEntity.ok(movieService.searchMovies(filter));
    }

    @Operation(summary = "Download CSV report",
            description = "Generates a CSV file based on filtering criteria")
    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateReport(@RequestBody MovieFilterRequest filter) {
        byte[] content = movieService.generateCsvReport(filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies_report.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @Operation(summary = "Import movies from JSON file")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultDto> uploadMovies(
            @Parameter(description = "JSON file containing an array of movies")
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(movieService.importMovies(file));
    }
}
