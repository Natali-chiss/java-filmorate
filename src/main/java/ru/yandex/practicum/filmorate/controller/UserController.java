package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.validation.ValidationService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, ValidationService validationService, UserService userService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        validationService.validateCreate(user);
        userRepository.saveUser(user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        validationService.validateUpdate(user);
        User updatedUser = userRepository.updateUser(user);
        log.info("Обновление пользователя завершено: {}", user);
        return updatedUser;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userRepository.getAllUsers();
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {
        log.info("Получен запрос на добавление друга");
        userService.addFriend(userId, friendId);
        log.info("Добавление друзей с id={} и id={} завершено", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {
        log.info("Получен запрос на удаление друга");
        userService.deleteFriend(userId, friendId);
        log.info("Пользователи с id={} и id={} больше не друзья", userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsList(@PathVariable Long userId) {
        log.info("Получен запрос на получение списка друзей у пользователя с id={}", userId);
        return userService.getFriendsList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        log.info("Получен запрос на получение списка общих друзей у пользователей с id={} и id={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}