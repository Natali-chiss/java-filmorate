package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepositoryInterface;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepositoryInterface filmRepository;
    private final UserRepositoryInterface userRepository;
    private final FilmMapper mapper;
    private final String filmNotFound = "Не найден фильм с id ="; // знаю, как пишутся константы, чекстайл ругается
    private final String userNotFound = "Не найден пользователь с id =";

    @Override
    public FilmDto saveFilm(Film film) {
        Film savedFilm = filmRepository.saveFilm(film);
        return mapper.toDto(savedFilm);
    }

    @Override
    public FilmDto updateFilm(Film film) {
       filmRepository.getFilmById(film.getId())
               .orElseThrow(() -> new InternalServerException(filmNotFound + film.getId()));
        Film updatedFilm = filmRepository.updateFilm(film);
        return mapper.toDto(updatedFilm);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound + userId));
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(filmNotFound + filmId));
        filmRepository.addLike(userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound + userId));
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(filmNotFound + filmId));
        filmRepository.removeLike(userId, filmId);
    }

    @Override
    public List<FilmDto> getTheMostPopularFilms(Integer count) {
        return filmRepository.getTheMostPopularFilms(count).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto getFilmById(Long filmId) {
        Film filmFromRepository = filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(filmNotFound + filmId));
        return mapper.toDto(filmFromRepository);
    }

    @Override
    public List<FilmDto> getAllFilms() {
        return filmRepository.getAllFilms().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
