package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmRepository implements FilmRepositoryInterface {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmsLikes = new HashMap<>();

    private long newId = 0;

    @Override
    public void saveFilm(Film film) {
        film.setId(++newId);
        films.put(film.getId(), film);
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmFromRepository = films.get(film.getId());
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

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        filmsLikes.computeIfAbsent(filmId, id -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        filmsLikes.computeIfAbsent(filmId, id -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<Film> getTheMostPopularFilms(Integer count) {
        return filmsLikes.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .sorted((entry1, entry2) -> Long.compare(
                        entry2.getValue().size(),
                        entry1.getValue().size()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(films::get)
                .collect(Collectors.toList());
    }
}