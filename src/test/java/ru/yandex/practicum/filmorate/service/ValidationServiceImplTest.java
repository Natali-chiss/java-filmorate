package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис валидации")
public class ValidationServiceImplTest {
    private ValidationServiceImpl validationService;
    private User user;
    private Film film;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl();

        user = new User("valid@email.com", "validLogin", "name",
                LocalDate.of(2003, 3, 11));

        film = new Film("valid name", "valid description",
                LocalDate.of(1999, 1, 1), 90);
    }

    @Test
    @DisplayName("Создание пользователя с пустым email выбрасывает исключение")
    void createUserWithBlankedEmailThrowsException() {
        user.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Создание пользователя с email без @ выбрасывает исключение")
    void createUserWithInvalidEmailThrowsException() {
        user.setEmail("invalid.email");
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Создание пользователя с пустым логином выбрасывает исключение")
    void createUserWithBlankedLoginThrowsException() {
        user.setLogin("");
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Создание пользователя с логином с пробелами выбрасывает исключение")
    void createUserWithLoginWithSpacesThrowsException() {
        user.setLogin("login with space");
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Имя пользователя при создании может быть пустым")
    void createBlankedNameUserThrowsNoException() {
        user.setName("");
        assertDoesNotThrow(() -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Дата рождения не может быть в будущем")
    void createFutureBirthdayUserThrowsException() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(user));
    }

    @Test
    @DisplayName("Создание фильма с пустым названием")
    void createBlankedNameFilmThrowsException() {
        film.setName("");
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с описанием в 201 символ выбрасывает исключение")
    void create201SymbolsDescriptionFilmThrowsException() {
        film.setDescription("a".repeat(201));
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с описанием в 200 символов не выбрасывает исключение")
    void create200SymbolsDescriptionFilmThrowsNoException() {
        film.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с описанием в 199 символов не выбрасывает исключение")
    void create199SymbolsDescriptionFilmThrowsNoException() {
        film.setDescription("a".repeat(199));
        assertDoesNotThrow(() -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с датой до 28.12.1895 выбрасывает исключение")
    void createEarlyReleaseDateFilmThrowsException() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с датой 28.12.1895 не выбрасывает исключение")
    void createRightReleaseDateFilmThrowsException() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с нулевой продолжительностью выбрасывает исключение")
    void createZeroDurationFilmThrowsException() {
        film.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(film));
    }

    @Test
    @DisplayName("Создание фильма с отрицательной продолжительностью")
    void createNegativeDurationFilmThrowsException() {
        film.setDuration(-1);
        assertThrows(ConditionsNotMetException.class, () -> validationService.validateCreate(film));
    }
}
