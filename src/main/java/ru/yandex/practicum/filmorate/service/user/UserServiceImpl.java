package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryInterface userRepository;

    @Autowired
    public UserServiceImpl(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userRepository.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));
        userRepository.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userRepository.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));
        userRepository.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        userRepository.getUserById(user1Id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + user1Id + " не найден"));
        userRepository.getUserById(user2Id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + user2Id + " не найден"));
        if (user1Id.equals(user2Id)) {
            throw new ConditionsNotMetException("Пользователи должны иметь разные id");
        }
        return userRepository.getCommonFriends(user1Id, user2Id);
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return userRepository.getFriendsList(userId);
    }
}
