package ru.yandex.practicum.filmorate.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepositoryInterface;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.mpa.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepositoryInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Import({
        JdbcFilmRepository.class,
        JdbcUserRepository.class,
        JdbcMpaRepository.class,  // Add this
        FilmRowMapper.class,
        UserRowMapper.class
})
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class JdbcFilmRepositoryTest {
    private final FilmRepositoryInterface filmRepository;
    private final UserRepositoryInterface userRepository;
    private Long testFilm1Id;
    private Long testFilm2Id;
    private Long testUser1Id;

    @BeforeEach
    void setUp() {
        Film film1 = Film.builder()
                .name("Test Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();

        Film film2 = Film.builder()
                .name("Test Film 2")
                .description("Description 2")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(90)
                .mpa(new Mpa(1, "G"))
                .build();

        User user = User.builder()
                .email("user@example.com")
                .login("user-login")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        testFilm1Id = filmRepository.saveFilm(film1).getId();
        testFilm2Id = filmRepository.saveFilm(film2).getId();
        testUser1Id = userRepository.saveUser(user).getId();
    }

    @Test
    void shouldSaveFilm() {
        Film newFilm = Film.builder()
                .name("New Film")
                .description("New Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();

        Film savedFilm = filmRepository.saveFilm(newFilm);

        assertThat(savedFilm.getId()).isNotNull().isPositive();
        assertEquals("New Film", savedFilm.getName());
        assertEquals("New Description", savedFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), savedFilm.getReleaseDate());
        assertEquals(100, savedFilm.getDuration());
        assertEquals(1, savedFilm.getMpa().getId());
        assertEquals("G", savedFilm.getMpa().getName());
    }

    @Test
    void shouldUpdateAllFields() {
        Film filmToUpdate = filmRepository.getFilmById(testFilm1Id).orElseThrow();
        filmToUpdate.setName("Updated Name");
        filmToUpdate.setDescription("Updated Description");

        Film updatedFilm = filmRepository.updateFilm(filmToUpdate);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertEquals(LocalDate.of(2000, 1, 1), updatedFilm.getReleaseDate());
        assertEquals(120, updatedFilm.getDuration());
        assertEquals(1, updatedFilm.getMpa().getId());
    }

    @Test
    void shouldReturnFilm_WhenExists() {
        Optional<Film> foundFilm = filmRepository.getFilmById(testFilm1Id);

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(testFilm1Id);
                    assertThat(film.getName()).isEqualTo("Test Film 1");
                });
    }

    @Test
    void shouldReturnAllFilms() {
        List<Film> films = filmRepository.getAllFilms();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(testFilm1Id, testFilm2Id);
    }

    @Test
    void shouldAddLikeToFilm() {
        filmRepository.addLike(testUser1Id, testFilm1Id);

        List<Film> popularFilms = filmRepository.getTheMostPopularFilms(1);
        assertThat(popularFilms)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(testFilm1Id);
    }

    @Test
    void shouldRemoveLikeFromFilm() {
        filmRepository.addLike(testUser1Id, testFilm1Id);
        List<Long> likes = filmRepository.getLikes(testFilm1Id);
        assertEquals(1, likes.size());
        filmRepository.removeLike(testUser1Id, testFilm1Id);
        List<Long> likesAfter = filmRepository.getLikes(testFilm1Id);
        assertEquals(0, likesAfter.size());
    }
}
