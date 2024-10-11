package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Collection<ReviewResponseDto> findByFilmId(
            @RequestParam("filmId") Optional<Integer> filmId,
            @RequestParam(value = "count", defaultValue = "10") @NotNull @Positive int count) {
        if (filmId.isPresent()) {
            return reviewService.findByFilmId(filmId.get(), count);
        } else {
            return reviewService.findAll(count);
        }
    }

    @GetMapping("/{id}")
    public ReviewResponseDto findById(@PathVariable("id") @Positive Long reviewId) {
        return reviewService.findById(reviewId);
    }

    @PostMapping
    public ReviewResponseDto create(@Valid @RequestBody ReviewSaveRequestDto reviewDto) {
        return reviewService.save(reviewDto);
    }

    @PutMapping
    public ReviewResponseDto update(@Valid @RequestBody ReviewUpdateRequestDto reviewDto) {
        return reviewService.update(reviewDto);
    }

    @PutMapping("{id}/like/{userId}")
    public ReviewResponseDto like(@PathVariable("id") @Positive Long reviewId,
                             @PathVariable("id") @Positive Integer userId) {
            return reviewService.like(reviewId, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public ReviewResponseDto dislike(@PathVariable("id") @Positive Long reviewId,
                             @PathVariable("id") @Positive Integer userId) {
        return reviewService.dislike(reviewId, userId);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable("id") @Positive Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public ReviewResponseDto removeLike(@PathVariable("id") @Positive Long reviewId,
                     @PathVariable("id") @Positive Integer userId) {
        return reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public ReviewResponseDto removeDislike(@PathVariable("id") @Positive Long reviewId,
                        @PathVariable("id") @Positive Integer userId) {
        return reviewService.removeDislike(reviewId, userId);
    }
}
