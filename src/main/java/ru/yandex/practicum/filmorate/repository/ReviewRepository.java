package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.dto.ReviewReactionDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Set;

public interface ReviewRepository {

    Review add(ReviewSaveRequestDto reviewDto);

    Review update(ReviewUpdateRequestDto reviewDto);

    void delete(Long id);

    Review getById(Long id);

    Collection<Review> getByFilmId(Integer id, int count);

    Collection<Review> getTop(int count);

    Review addLike(Long id, Integer userId);

    Review removeLike(Long id, Integer userId);

    Review addDislike(Long id, Integer userId);

    Review removeDislike(Long id, Integer userId);

    Collection<ReviewReactionDto> getReactions(Set<Long> reviewsIds);
}
