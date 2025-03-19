package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();

    public void saveFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
    }

    public Film updateFilm(Film film) {
        Film filmFromRepository = getFilmById(film.getId()).get(); // проверка isPresent в ValidationService
        if (film.getName() != null) {
            filmFromRepository.setName(film.getName());
        }
        if (film.getDescription() != null) {
            filmFromRepository.setDescription(film.getDescription());
        }
        if (film.getDuration() != 0) {
            filmFromRepository.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
            filmFromRepository.setReleaseDate(film.getReleaseDate());
        }
        return filmFromRepository;
    }

    public List<Film> getAllFilms() {
        return films.values().stream().toList();
    }

    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}