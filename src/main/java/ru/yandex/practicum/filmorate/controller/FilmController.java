package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmRepository filmRepository;
    private final ValidationService validationService;

    @Autowired
    public FilmController(FilmRepository filmRepository, ValidationService validationService) {
        this.filmRepository = filmRepository;
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
}
