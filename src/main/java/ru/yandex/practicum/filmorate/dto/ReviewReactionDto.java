package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReactionDto {
    Long reviewId;
    Integer userId;
    boolean isLiked;
    boolean isDisliked;
}
