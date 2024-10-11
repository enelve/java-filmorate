package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
@Primary
public class ReviewMemoryRepository implements ReviewRepository {

    private final Map<Long, Review> storage = new HashMap<>();
    private Long id = 0L;

    @Override
    public Review add(ReviewSaveRequestDto reviewDto) {
        Review review = ReviewMapper.saveRequestDtoToReview(reviewDto);
        review.setId(nextId());
        log.debug("set id {}", review);
        storage.put(review.getId(), review);
        return getById(review.getId());
    }

    @Override
    public Review update(ReviewUpdateRequestDto reviewDto) {
        Review review = getById(reviewDto.getReviewId());
        review.setPositive(reviewDto.getIsPositive());
        review.setContent(reviewDto.getContent());
        storage.put(review.getId(), new Review(review));
        return review;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public Review getById(Long id) {
        Review review = storage.get(id);
        if (Objects.isNull(review)) {
            log.error("getById. Review by id = {} not found", id);
            throw  new NotFoundException(String.format("Ревью с id = %d не найден", id));
        }
        return new Review(review);
    }

    @Override
    public Collection<Review> getByFilmId(Integer id, int count) {
        return storage.values().stream()
                .filter(review -> review.getFilmId().equals(id))
                .sorted(Comparator.comparingInt(Review::getUseful))
                .limit(count)
                .toList();
    }

    @Override
    public Collection<Review> getTop(int count) {
        return storage.values().stream()
                .sorted(Comparator.comparingInt(Review::getUseful))
                .limit(count)
                .toList();
    }

    @Override
    public Review addLike(Long id, Integer userId) {
        Review review = getById(id);
        review.getDislikes().remove(userId);
        review.getLikes().add(userId);
        review.evaluateUseful();
        storage.put(id, new Review(review));
        return review;
    }

    @Override
    public Review removeLike(Long id, Integer userId) {
        Review review = getById(id);
        review.getLikes().remove(userId);
        review.evaluateUseful();
        storage.put(id, new Review(review));
        return review;
    }

    @Override
    public Review addDislike(Long id, Integer userId) {
        Review review = getById(id);
        review.getDislikes().add(userId);
        review.getLikes().remove(userId);
        review.evaluateUseful();
        storage.put(id, new Review(review));
        return review;
    }

    @Override
    public Review removeDislike(Long id, Integer userId) {
        Review review = getById(id);
        review.getDislikes().remove(userId);
        review.evaluateUseful();
        storage.put(id, new Review(review));
        return review;
    }

    private Long nextId() {
        return ++id;
    }
}
