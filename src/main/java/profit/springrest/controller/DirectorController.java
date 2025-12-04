package profit.springrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;
import profit.springrest.service.director.DirectorService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/director")
@Tag(name = "Directors", description = "Director Management")
public class DirectorController {

    private final DirectorService directorService;

    @Operation(summary = "Get all directors")
    @GetMapping
    public ResponseEntity<List<DirectorResponseDto>> findAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    @Operation(summary = "Create a new director")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Director successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error or director name already exists")
    })
    @PostMapping
    public ResponseEntity<DirectorResponseDto> createDirector(@RequestBody @Valid DirectorRequestDto directorRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directorService.createDirector(directorRequestDto));
    }

    @Operation(summary = "Update director details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Director updated successfully"),
            @ApiResponse(responseCode = "404", description = "Director not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DirectorResponseDto> updateDirector(@PathVariable Long id,
                                                              @RequestBody @Valid DirectorRequestDto directorRequestDto) {
        return ResponseEntity.ok(directorService.updateDirector(directorRequestDto, id));
    }

    @Operation(summary = "Delete a director")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Director deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Director not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectorById(@PathVariable Long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }
}
