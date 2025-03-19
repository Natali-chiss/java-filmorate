package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void saveUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
    }

    public User updateUser(User user) {
        User userFromRepository = getUserById(user.getId()).get(); // проверка isPresent в ValidationService
        if (user.getName() != null) {
            userFromRepository.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromRepository.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            userFromRepository.setLogin(user.getLogin());
        }
        if (user.getBirthday() != null) {
            userFromRepository.setBirthday(user.getBirthday());
        }
        return userFromRepository;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
