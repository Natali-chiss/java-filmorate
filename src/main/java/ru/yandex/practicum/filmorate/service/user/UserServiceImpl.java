package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInterface userRepository;
    private final UserMapper mapper;
    private final String userNotFound = "Не найден пользователь с id =";

    @Override
    public UserDto saveUser(User user) {
        User savedUser = userRepository.saveUser(user);
        return mapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(User user) {
        userRepository.getUserById(user.getId())
                .orElseThrow(() -> new NotFoundException(userNotFound + user.getId()));
        User updatedUser = userRepository.updateUser(user);
        return mapper.mapToUserDto(updatedUser);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound + userId));
        userRepository.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException(userNotFound + friendId));
        userRepository.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound + userId));
        userRepository.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException(userNotFound + friendId));
        userRepository.deleteFriend(userId, friendId);
    }

    @Override
    public List<UserDto> getCommonFriends(Long user1Id, Long user2Id) {
        userRepository.getUserById(user1Id)
                .orElseThrow(() -> new NotFoundException(userNotFound + user1Id));
        userRepository.getUserById(user2Id)
                .orElseThrow(() -> new NotFoundException(userNotFound + user2Id));
        if (user1Id.equals(user2Id)) {
            throw new ConditionsNotMetException("Пользователи должны иметь разные id");
        }
        return userRepository.getCommonFriends(user1Id, user2Id).stream()
                .map(mapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getFriendsList(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound + userId));
        return userRepository.getFriendsList(userId).stream()
                .map(mapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(mapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
