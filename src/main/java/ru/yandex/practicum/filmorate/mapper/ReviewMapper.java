package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
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

    public static Review saveRequestDtoToReview(ReviewSaveRequestDto reviewDto) {
        Review review = new Review();
        review.setContent(reviewDto.getContent());
        review.setPositive(reviewDto.getIsPositive());
        review.setUserId(reviewDto.getUserId());
        review.setFilmId(reviewDto.getFilmId());
        return review;
    }
}
