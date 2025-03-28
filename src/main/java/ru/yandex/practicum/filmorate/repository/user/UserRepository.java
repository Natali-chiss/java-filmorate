package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepository implements UserRepositoryInterface {
    private long newId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> userFriendsIds = new HashMap<>();

    @Override
    public void saveUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(++newId);
        users.put(user.getId(), user);
    }

    @Override
    public User updateUser(User user) {
        User userFromRepository = users.get(user.getId());
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

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userFriendsIds.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);
        userFriendsIds.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userFriendsIds.computeIfAbsent(userId, id -> new HashSet<>()).remove(friendId);
        userFriendsIds.computeIfAbsent(friendId, id -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        return userFriendsIds.getOrDefault(user1Id, Set.of()).stream()
                .filter(userFriendsIds.getOrDefault(user2Id, Set.of())::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        return userFriendsIds.getOrDefault(userId, Set.of()).stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
