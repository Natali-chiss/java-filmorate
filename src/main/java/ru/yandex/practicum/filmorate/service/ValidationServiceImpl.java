package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final FilmRepository filmRepository;

    public ValidationServiceImpl() {
        this.userRepository = new UserRepository();
        this.filmRepository = new FilmRepository();
    }

    @Override
    public void validateCreate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        } else if (!user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Email должен содержать символ @");
        } else if (isEmailUsed(user.getEmail())) {
            throw new DuplicatedDataException("Этот email уже используется");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    @Override
    public void validateUpdate(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (userRepository.getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        User userFromRepository = userRepository.getUserById(user.getId()).get();
        if (!userFromRepository.getEmail().equals(user.getEmail())) {
            if (isEmailUsed(user.getEmail())) {
                throw new DuplicatedDataException("Этот email уже используется");
            }
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new ConditionsNotMetException("Email должен содержать символ @");
            }
        }
        if (user.getLogin() != null) {
            if (user.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("Логин не может содержать пробелы");
            }
        }
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }
        }
    }

    @Override
    public void validateCreate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название должно быть указано");
        } else if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ConditionsNotMetException("Дата релиза дожна быть не раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @Override
    public void validateUpdate(Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (filmRepository.getFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
            }
        }
        if (film.getDuration() != 0) {
            if (film.getDuration() < 0) {
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
            }
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ConditionsNotMetException("Дата релиза дожна быть не раньше 28 декабря 1895 года");
            }
        }
    }

    private boolean isEmailUsed(String email) {
        return userRepository.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}