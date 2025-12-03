package profit.springrest.dto.movie;

import lombok.Builder;

@Builder
public record ImportResultDto(
        Integer successCount,
        Integer failedCount
) {
}
