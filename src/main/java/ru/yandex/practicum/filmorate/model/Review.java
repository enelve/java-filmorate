package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = { "id" })
public class Review {

    @NotNull
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private boolean isPositive;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    private final Set<Integer> likes = new HashSet<>();
    private final Set<Integer> dislikes = new HashSet<>();
    private int useful;

    public Review() {
    }

    public Review(Review review) {
        this.setId(review.getId());
        this.setPositive(review.isPositive());
        this.setContent(review.getContent());
        this.setFilmId(review.getFilmId());
        this.setUserId(review.getUserId());
        this.setUseful(review.getUseful());
        this.likes.addAll(review.getLikes());
        this.dislikes.addAll(review.getDislikes());
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void addDislike(Integer userId) {
        dislikes.add(userId);
    }

    public void removeLike(Integer userId) {
        likes.remove(userId);
    }

    public void removeDislike(Integer userId) {
        dislikes.remove(userId);
    }

    public void evaluateUseful() {
        this.useful = likes.size()-dislikes.size();
    }
}
