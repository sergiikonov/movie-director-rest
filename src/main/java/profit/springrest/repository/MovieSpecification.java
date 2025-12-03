package profit.springrest.repository;

import org.springframework.data.jpa.domain.Specification;
import profit.springrest.data.Genres;
import profit.springrest.data.Movie;

public class MovieSpecification {
    public static Specification<Movie> hasGenre(Genres genre) {
        return (root, query, cb) -> {
            if (genre == null) return null;
            return cb.equal(root.get("genre"), genre);
        };
    }

    public static Specification<Movie> hasReleaseYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return null;
            return cb.equal(root.get("releaseYear"), year);
        };
    }

    public static Specification<Movie> hasDirectorId(Long directorId) {
        return (root, query, cb) -> {
            if (directorId == null) return null;
            return cb.equal(root.get("director").get("id"), directorId);
        };
    }
}
