package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final FilmService filmService;

    public ReviewResponseDto save(ReviewSaveRequestDto reviewDto) {
        log.debug("starting saving {}", reviewDto);
        onSaveCheck(reviewDto.getUserId(), reviewDto.getFilmId());
        return ReviewMapper.toResponseDto(reviewRepository.add(reviewDto));
    }

    public ReviewResponseDto update(ReviewUpdateRequestDto reviewDto) {
        log.debug("starting updating {}", reviewDto);
        onUpdateCheck(reviewDto);
        return ReviewMapper.toResponseDto(reviewRepository.update(reviewDto));
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
}
