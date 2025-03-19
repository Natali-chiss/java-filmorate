package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface ValidationService {
    void validateCreate(User user);

    void validateUpdate(User user);

    void validateCreate(Film film);

    void validateUpdate(Film film);
}