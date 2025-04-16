package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.Optional;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcMpaRepository implements MpaRepositoryInterface {
    private static final String GET_ALL_SQL = "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
    private static final String GET_BY_ID_SQL = "SELECT mpa_id, name FROM mpa WHERE mpa_id = :id";

    private final NamedParameterJdbcOperations jdbc;
    private final MpaRowMapper mapper;


    @Override
    public List<Mpa> getAll() {
        return jdbc.query(GET_ALL_SQL, mapper);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        try {
            Mpa result = jdbc.queryForObject(GET_BY_ID_SQL, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}