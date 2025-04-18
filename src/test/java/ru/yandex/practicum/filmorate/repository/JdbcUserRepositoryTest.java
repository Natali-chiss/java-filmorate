package ru.yandex.practicum.filmorate.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcUserRepository.class, UserRowMapper.class})
@ActiveProfiles("test")
@Transactional
class JdbcUserRepositoryTest {
    private final UserRepositoryInterface userRepository;

    private Long user1Id;
    private Long user2Id;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .email("test1@example.com")
                .login("test-login1")
                .name("Test User 1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("test2@example.com")
                .login("test-login2")
                .name("Test User 2")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        user1Id = userRepository.saveUser(user1).getId();
        user2Id = userRepository.saveUser(user2).getId();
    }

    @Test
    void shouldSaveUser() {
        User savedUser = userRepository.getUserById(user1Id).orElseThrow();

        assertThat(savedUser.getId()).isNotNull().isPositive();
        assertEquals("test-login1", savedUser.getLogin());
        assertEquals("Test User 1", savedUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), savedUser.getBirthday());

        Optional<User> retrievedUser = userRepository.getUserById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
    }

    @Test
    void shouldUpdateNecessaryFields() {
        User userToUpdate = userRepository.getUserById(user1Id).orElseThrow();
        userToUpdate.setName("Updated Name");
        userToUpdate.setEmail("updated@example.com");

        User updatedUser = userRepository.updateUser(userToUpdate);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("test-login1", updatedUser.getLogin());
        assertEquals(LocalDate.of(1990, 1, 1), updatedUser.getBirthday());
    }

    @Test
    void shouldReturnAllCreatedUsers() {
        List<User> users = userRepository.getAllUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1Id, user2Id);
    }

    @Test
    void shouldCreateFriendship() {
        userRepository.addFriend(user1Id, user2Id);

        List<User> friends = userRepository.getFriendsList(user1Id);
        assertThat(friends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2Id);
    }

    @Test
    void shouldDeleteFriendship() {
        userRepository.addFriend(user1Id, user2Id);
        userRepository.deleteFriend(user1Id, user2Id);

        List<User> friends = userRepository.getFriendsList(user1Id);
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldReturnEmptyList_WhenNoFriends() {
        List<User> friends = userRepository.getFriendsList(user1Id);
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldReturnCommonFriends() {
        User commonFriend = userRepository.saveUser(
                User.builder()
                        .email("common@example.com")
                        .login("common-friend")
                        .name("Common Friend")
                        .birthday(LocalDate.of(1992, 3, 3))
                        .build()
        );

        userRepository.addFriend(user1Id, commonFriend.getId());
        userRepository.addFriend(user2Id, commonFriend.getId());

        List<User> commonFriends = userRepository.getCommonFriends(user1Id, user2Id);

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(commonFriend.getId());
    }
}