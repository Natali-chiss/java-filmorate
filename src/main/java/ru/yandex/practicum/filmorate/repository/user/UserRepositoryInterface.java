package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryInterface {

    void saveUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);

    List<User> getFriendsList(Long userId);
}
