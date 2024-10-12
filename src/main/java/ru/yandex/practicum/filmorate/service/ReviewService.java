package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedRepository feedRepository;
    private static final EventTypesEnum EVENT_TYPES = EventTypesEnum.REVIEW;

    public ReviewResponseDto save(ReviewSaveRequestDto reviewDto) {
        log.debug("starting saving {}", reviewDto);
        onSaveCheck(reviewDto.getUserId(), reviewDto.getFilmId());
        Review review = reviewRepository.add(reviewDto);
        feedRepository.add(reviewDto.getUserId(), review.getId(), EVENT_TYPES, OperationsEnum.ADD);
        return ReviewMapper.toResponseDto(review);
    }

    public ReviewResponseDto update(ReviewUpdateRequestDto reviewDto) {
        log.debug("starting updating {}", reviewDto);
        onUpdateCheck(reviewDto);
        Review review = reviewRepository.update(reviewDto);
        feedRepository.add(reviewDto.getUserId(), review.getId(), EVENT_TYPES, OperationsEnum.UPDATE);
        return ReviewMapper.toResponseDto(review);
    }

    public ReviewResponseDto findById(Long reviewId) {
        return ReviewMapper.toResponseDto(reviewRepository.getById(reviewId));
    }

    public Collection<ReviewResponseDto> findByFilmId(Integer filmId, int count) {
        filmService.getById(filmId);
        return reviewRepository.getByFilmId(filmId, count).stream().map(ReviewMapper::toResponseDto).toList();
    }

    public Collection<ReviewResponseDto> findAll(int count) {
        return reviewRepository.getTop(count).stream().map(ReviewMapper::toResponseDto).toList();
    }

    public void deleteReview(Long reviewId) {
        ReviewResponseDto review = findById(reviewId);
        feedRepository.add(review.getUserId(), reviewId, EVENT_TYPES, OperationsEnum.REMOVE);
        reviewRepository.delete(reviewId);
    }

    public ReviewResponseDto like(Long reviewId, Integer userId) {
        onReactionCheck(reviewId, userId);
        return ReviewMapper.toResponseDto(reviewRepository.addLike(reviewId, userId));
    }

    public ReviewResponseDto dislike(Long reviewId, Integer userId) {
        onReactionCheck(reviewId, userId);
        return ReviewMapper.toResponseDto(reviewRepository.addDislike(reviewId, userId));
    }

    public ReviewResponseDto removeLike(Long reviewId, Integer userId) {
        onReactionCheck(reviewId, userId);
        return ReviewMapper.toResponseDto(reviewRepository.removeLike(reviewId, userId));
    }

    public ReviewResponseDto removeDislike(Long reviewId, Integer userId) {
        onReactionCheck(reviewId, userId);
        return ReviewMapper.toResponseDto(reviewRepository.removeDislike(reviewId, userId));
    }

    private void onSaveCheck(Integer userId, Integer filmId) {
        List<String> errors = new ArrayList<>();
        try {
            userService.getById(userId);
        } catch (NotFoundException e) {
            errors.add(e.getMessage());
        }
        try {
            filmService.getById(filmId);
        } catch (NotFoundException e) {
            errors.add(e.getMessage());
        }
        if (!errors.isEmpty()) {
            StringBuilder errorBuilder = new StringBuilder();
            errors.forEach(error -> {
                        errorBuilder.append(error);
                        errorBuilder.append("\n");
                    }
            );
            throw new NotFoundException(errorBuilder.toString()); //только из-за postman тестов
        }
    }

    private void onUpdateCheck(ReviewUpdateRequestDto reviewDto) {
        findById(reviewDto.getReviewId());
        onSaveCheck(reviewDto.getUserId(), reviewDto.getFilmId());
    }

    private void onReactionCheck(Long reviewId, Integer userId) {
        findById(reviewId);
        userService.getById(userId);
    }


}
