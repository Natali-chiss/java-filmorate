package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepositoryInterface;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.validation.ValidationService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmRepositoryInterface filmRepository;
    private final FilmService filmService;
    private final ValidationService validationService;

    @Autowired
    public FilmController(FilmRepositoryInterface filmRepository, FilmService filmService,
                          ValidationService validationService) {
        this.filmRepository = filmRepository;
        this.filmService = filmService;
        this.validationService = validationService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        validationService.validateCreate(film);
        filmRepository.saveFilm(film);
        log.info("Создан новый фильм: {}", film);
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        validationService.validateUpdate(film);
        Film updatedFilm = filmRepository.updateFilm(film);
        log.info("Обновление фильма завершено");
        return updatedFilm;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("Получен запрос на получение списка всех фильмов");
        return filmRepository.getAllFilms();
    }

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable Long filmId) {
        log.info("Получен запрос на получение фильма с id={}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long userId,
                        @PathVariable Long filmId) {
        log.info("Пользователь с id={} запросил добавление лайка на фильма с id={}", userId, filmId);
        filmService.addLike(userId, filmId);
        log.info("Добавление лайка завершено");
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable Long userId,
                           @PathVariable Long filmId) {
        log.info("Пользователь с id={} запросил удаление лайка с фильма с id={}", userId, filmId);
        filmService.removeLike(userId, filmId);
        log.info("Удаление лайка завершено");
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTheMostPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение {} самых популярных фильмов", count);
        return filmService.getTheMostPopularFilms(count);
    }
}