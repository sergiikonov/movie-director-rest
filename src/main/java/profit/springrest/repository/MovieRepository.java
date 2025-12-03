package profit.springrest.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import profit.springrest.data.Movie;

@Repository
public interface MovieRepository extends JpaRepository<@NonNull Movie, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull Movie> {
}
