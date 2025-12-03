package profit.springrest.dto.movie;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> list;
    private int totalPages;
    private long totalElements;
}