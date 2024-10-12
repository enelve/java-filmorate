package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@ComponentScan("ru/yandex/practicum/filmorate")
class ReviewIT {

    @Autowired
    ReviewService reviewService;
    @Autowired
    UserService userService;
    @Autowired
    FilmService filmService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private ReviewSaveRequestDto createReviewSaveRequestDto(Integer userId, Integer filmId) {
        ReviewSaveRequestDto review = new ReviewSaveRequestDto();
        review.setIsPositive(true);
        review.setContent("ok");
        review.setFilmId(filmId);
        review.setUserId(userId);
        return review;
    }

    private ReviewUpdateRequestDto createReviewUpdateRequestDto(Long reviewId,Integer userId, Integer filmId) {
        ReviewUpdateRequestDto review = new ReviewUpdateRequestDto();
        review.setIsPositive(true);
        review.setContent("ok ok");
        review.setFilmId(filmId);
        review.setUserId(userId);
        review.setReviewId(reviewId);
        return review;
    }

    private User getTestUser(String email, String login) {
        return new User(null, email, login, "name", LocalDate.now().minusYears(20));
    }

    private Film getTestFilm(String name) {
        return new Film(null, name, "description", LocalDate.now().minusYears(20), 1, new FilmRating(1, "G"), new HashSet<>(), new ArrayList<>());
    }

    private void cleanUpDatabase() {
        jdbcTemplate.execute("DELETE FROM reviews; DELETE FROM reviews_reactions;");
        reviewService.findAll(10)
                .forEach(r -> reviewService.deleteReview(r.getReviewId()));
    }

    @AfterEach
    void afterEach() {
        cleanUpDatabase();
    }

    @Test
    public void sizeIncreasesWhenSaveReview() {
        User user1 = userService.add(getTestUser("email1", "login1"));
        Film film1 = filmService.add(getTestFilm("film1"));

        reviewService.save(createReviewSaveRequestDto(user1.getId(), film1.getId()));
        assertEquals(1, reviewService.findAll(10).size());
    }

    @Test
    public void returnUpdatedContentWhenUpdate() {
        User user1 = userService.add(getTestUser("email1", "login1"));
        Film film1 = filmService.add(getTestFilm("film1"));
        ReviewResponseDto reviewResponseDto = reviewService.save(createReviewSaveRequestDto(user1.getId(),
                film1.getId()));
        ReviewUpdateRequestDto reviewUpdateRequestDto = createReviewUpdateRequestDto(reviewResponseDto.getReviewId(),
                user1.getId(), film1.getId());
        assertEquals(reviewUpdateRequestDto.getContent(), reviewService.update(reviewUpdateRequestDto).getContent());
    }

    @Test
    public void returnSortedByUseful() {
        ReviewResponseDto[] reviews = new ReviewResponseDto[10];
        User[] users = new User[10];
        Film film = filmService.add(getTestFilm("film"));
        for (int i = 0; i < 10; i++) {
            users[i] = userService.add(getTestUser("email" + i, "login" + i));
            reviews[i] = reviewService.save(createReviewSaveRequestDto(users[i].getId(), film.getId()));
        }
        for (int i = 0; i < 5; i++) {
            reviewService.like(reviews[5].getReviewId(), users[i].getId());
        }
        for (int i = 5; i < 9; i++) {
            reviewService.dislike(reviews[5].getReviewId(), users[i].getId());
        }
        for (int i = 0; i < 4; i++) {
            reviewService.like(reviews[4].getReviewId(), users[i].getId());
        }
        for (int i = 0; i < 3; i++) {
            reviewService.like(reviews[3].getReviewId(), users[i].getId());
        }
        Collection<ReviewResponseDto> reviewResponseDtos = reviewService.findByFilmId(film.getId(), 3);
        assertEquals(3,reviewResponseDtos.size());
        List<ReviewResponseDto> reviewList = reviewResponseDtos.stream().toList();
        assertEquals(reviewList.get(0).getReviewId(),reviews[4].getReviewId());
        assertEquals(reviewList.get(1).getReviewId(),reviews[3].getReviewId());
        assertEquals(reviewList.get(2).getReviewId(),reviews[5].getReviewId());
    }
}