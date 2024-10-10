package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

@UtilityClass
public class ReviewMapper {
    public static ReviewResponseDto toResponseDto(Review review) {
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto();
        reviewResponseDto.setReviewId(review.getId());
        reviewResponseDto.setContent(review.getContent());
        reviewResponseDto.setPositive(review.isPositive());
        reviewResponseDto.setUserId(review.getUserId());
        reviewResponseDto.setFilmId(review.getFilmId());
        reviewResponseDto.setUseful(review.getUseful());
        return reviewResponseDto;
    }
}
