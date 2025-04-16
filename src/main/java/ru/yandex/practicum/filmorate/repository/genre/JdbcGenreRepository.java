package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcGenreRepository implements GenreRepositoryInterface {
    private static final String GET_ALL_SQL = "SELECT genre_id, name FROM genres ORDER BY genre_id";
    private static final String GET_BY_ID_SQL = "SELECT genre_id, name FROM genres WHERE genre_id = :id";
    private static final String FIND_BY_FILM_ID = """
            SELECT g.* FROM genres g
            JOIN film_genres fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = :filmId
            ORDER BY g.genre_id
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper mapper;

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query(GET_ALL_SQL, mapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        try {
            Genre result = jdbc.queryForObject(GET_BY_ID_SQL, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findByFilmId(Long filmId) {
        return jdbc.query(
                FIND_BY_FILM_ID,
                new MapSqlParameterSource("filmId", filmId),
                new GenreRowMapper()
        );
    }
}
