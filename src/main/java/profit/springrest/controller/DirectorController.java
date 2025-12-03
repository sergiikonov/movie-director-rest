package profit.springrest.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;
import profit.springrest.service.director.DirectorService;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/director")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<DirectorResponseDto> findAllDirectors() {
        return directorService.getAllDirectors();
    }

    @PostMapping
    public DirectorResponseDto createDirector(@RequestBody @Valid DirectorRequestDto directorRequestDto) {
        return directorService.createDirector(directorRequestDto);
    }

    @PutMapping("/{id}")
    public DirectorResponseDto updateDirector(@PathVariable Long id,
                                              @RequestBody @Valid DirectorRequestDto directorRequestDto) {
        return directorService.updateDirector(directorRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> deleteDirectorById(@PathVariable Long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }
}
