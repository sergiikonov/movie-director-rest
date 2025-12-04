package profit.springrest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import profit.springrest.data.Genres;
import profit.springrest.dto.movie.ImportResultDto;
import profit.springrest.dto.movie.MovieResponseDto;
import profit.springrest.service.movie.MovieService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import profit.springrest.dto.movie.*;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @Test
    @DisplayName("GET /api/movie/{id} - Success")
    void findMovie_Success() throws Exception {
        MovieResponseDto response = new MovieResponseDto(1L, "Test", 2000, Genres.DRAMA, null);
        when(movieService.findMovieById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/movie/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    @DisplayName("POST /api/movie - Created")
    void createMovie_Success() throws Exception {
        MovieRequestDto request = new MovieRequestDto("New Movie", 2022, Genres.HORROR, 1L);
        MovieResponseDto response = new MovieResponseDto(1L, "New Movie", 2022, Genres.HORROR, null);

        when(movieService.createMovie(any())).thenReturn(response);

        mockMvc.perform(post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/movie - Validation Error (Invalid Year)")
    void createMovie_InvalidYear() throws Exception {
        MovieRequestDto request = new MovieRequestDto("Old", 1000, Genres.DRAMA, 1L);

        mockMvc.perform(post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // CustomGlobalExceptionHandler повертає список помилок
                .andExpect(jsonPath("$[0]").value("Release year must be valid (after 1888)"));
    }

    @Test
    @DisplayName("POST /api/movie/_list - Search Success")
    void searchMovies_Success() throws Exception {
        MovieFilterRequest filter = new MovieFilterRequest(null, null, null, 0, 10);
        PageResponse<MovieListDto> pageResponse = PageResponse.<MovieListDto>builder()
                .list(List.of(new MovieListDto(1L, "M", 2000, Genres.DRAMA, "D")))
                .totalElements(1)
                .totalPages(1)
                .build();

        when(movieService.searchMovies(any())).thenReturn(pageResponse);

        mockMvc.perform(post("/api/movie/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("POST /api/movie/_report - Download CSV")
    void generateReport_Success() throws Exception {
        MovieFilterRequest filter = new MovieFilterRequest(null, null, null, 0, 10);
        byte[] csvContent = "ID,Title\n1,Test".getBytes();

        when(movieService.generateCsvReport(any())).thenReturn(csvContent);

        mockMvc.perform(post("/api/movie/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=movies_report.csv"))
                .andExpect(content().bytes(csvContent));
    }

    @Test
    @DisplayName("POST /api/movie/upload - Upload File")
    void uploadMovies_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.json",
                MediaType.APPLICATION_JSON_VALUE,
                "[{\"title\":\"Test\"}]".getBytes()
        );

        ImportResultDto result = new ImportResultDto(5, 0);
        when(movieService.importMovies(any())).thenReturn(result);

        mockMvc.perform(multipart("/api/movie/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(5));
    }
}
