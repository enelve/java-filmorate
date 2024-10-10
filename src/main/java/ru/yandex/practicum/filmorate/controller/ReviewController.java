package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/reviews")
public class ReviewController {

        private final ReviewService reviewService;

        @GetMapping("/{id}")
        public ReviewResponseDto findFilm(@PathVariable("id") @Positive Long reviewId) {
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
}
