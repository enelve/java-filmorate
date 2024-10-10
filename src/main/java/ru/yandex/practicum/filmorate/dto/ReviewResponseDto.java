package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDto {

    private Long reviewId;
    private String content;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private Integer useful;
}
