package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepositoryInterface;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepositoryInterface filmRepository;
    private final UserRepositoryInterface userRepository;

    @Autowired
    public FilmServiceImpl(FilmRepositoryInterface filmRepository, UserRepositoryInterface userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        filmRepository.addLike(userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        filmRepository.removeLike(userId, filmId);
    }

    @Override
    public List<Film> getTheMostPopularFilms(Integer count) {
        return filmRepository.getTheMostPopularFilms(count);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
    }
}
