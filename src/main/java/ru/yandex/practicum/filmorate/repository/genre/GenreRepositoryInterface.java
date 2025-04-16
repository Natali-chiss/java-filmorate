package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepositoryInterface {
    List<Genre> getAllGenres();

    Optional<Genre> getById(int id);

    List<Genre> findByFilmId(Long filmId);
}