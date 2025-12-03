package profit.springrest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import profit.springrest.dto.director.DirectorRequestDto;
import profit.springrest.dto.director.DirectorResponseDto;
import profit.springrest.exception.EntityNotFoundException;
import profit.springrest.service.director.DirectorService;
import tools.jackson.databind.ObjectMapper;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DirectorController.class)
class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DirectorService directorService;

    @Test
    @DisplayName("GET /api/director - Success")
    void findAllDirectors_Success() throws Exception {
        when(directorService.getAllDirectors()).thenReturn(List.of(
                new DirectorResponseDto(1L, "Test Director")
        ));

        mockMvc.perform(get("/api/director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Director"));
    }

    @Test
    @DisplayName("POST /api/director - Created (201)")
    void createDirector_Success() throws Exception {
        DirectorRequestDto request = new DirectorRequestDto("New Director");
        DirectorResponseDto response = new DirectorResponseDto(1L, "New Director");

        when(directorService.createDirector(any())).thenReturn(response);

        mockMvc.perform(post("/api/director")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/director/{id} - No Content (204)")
    void deleteDirector_Success() throws Exception {
        mockMvc.perform(delete("/api/director/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/director - Validation Error (Empty Name)")
    void createDirector_ValidationError() throws Exception {
        DirectorRequestDto request = new DirectorRequestDto("");

        mockMvc.perform(post("/api/director")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("POST /api/director - Duplicate Name (Business Logic Error)")
    void createDirector_DuplicateError() throws Exception {
        DirectorRequestDto request = new DirectorRequestDto("Existing");

        when(directorService.createDirector(any()))
                .thenThrow(new IllegalArgumentException("Already exists"));

        mockMvc.perform(post("/api/director")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.error").value("Already exists"));
    }

    @Test
    @DisplayName("PUT /api/director/{id} - Not Found")
    void updateDirector_NotFound() throws Exception {
        DirectorRequestDto request = new DirectorRequestDto("Name");

        when(directorService.updateDirector(any(), eq(99L)))
                .thenThrow(new EntityNotFoundException("Director with id 99 not found"));

        mockMvc.perform(put("/api/director/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()) // 404
                .andExpect(content().string("Director with id 99 not found"));
    }
}
