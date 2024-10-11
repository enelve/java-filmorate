package ru.yandex.practicum.filmorate.repository.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.ReviewReactionDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequestDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class ReviewDatabaseRepository implements ReviewRepository {

    private static final String FIND_ALL_QUERY = "SELECT r.*, IFNULL(rr.useful,0) useful " +
            "FROM REVIEWS r " +
            "Left JOIN " +
            "(@USEFUL_QUERY) AS rr " +
            "ON r.REVIEW_ID  = rr.REVIEW_ID " +
            "ORDER BY useful " +
            "LIMIT ?";
    private static final String FIND_BY_ID_QUERY = "SELECT r.*, IFNULL(rr.useful,0) useful " +
            "FROM " +
            "(SELECT * FROM REVIEWS WHERE REVIEW_ID = ?) AS r " +
            "Left JOIN " +
            "(@USEFUL_QUERY) AS rr " +
            "ON r.REVIEW_ID  = rr.REVIEW_ID";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT r.*, IFNULL(rr.useful,0) useful " +
            "FROM " +
            "(SELECT * FROM REVIEWS WHERE FILM_ID = ?) AS r " +
            "Left JOIN " +
            "(@USEFUL_QUERY) AS rr " +
            "ON r.REVIEW_ID  = rr.REVIEW_ID " +
            "ORDER BY useful " +
            "LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews(content, is_positive, user_id,"+
            " film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, " +
            "film_id = ? WHERE review_id = ?";
    private static final String USEFUL_QUERY = "SELECT  REVIEW_ID, " +
            "COUNT(CASE WHEN IS_LIKED THEN 1 END)-COUNT(CASE WHEN IS_DISLIKED THEN 1 END) useful " +
            "FROM REVIEWS_REACTIONS GROUP BY REVIEW_ID";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String LIKE_DISLIKE_QUERY = "MERGE INTO reviews_reactions " +
        "(REVIEW_ID, USER_ID, IS_LIKED, IS_DISLIKED ) KEY(REVIEW_ID, USER_ID) VALUES (?, ?, ?, ?)";
    private static final String REACTIONS_BY_REVIEW_ID_QUERY = "SELECT REVIEW_ID, USER_ID, IS_LIKED, IS_DISLIKED " +
            "FROM REVIEWS_REACTIONS rr " +
            "INNER JOIN " +
            "(@REVIEWS_IDS) AS ri " +
            "ON ri.ID = rr.REVIEW_ID";
    protected final JdbcTemplate jdbc;
    protected final RowMapper<Review> mapper;
    protected final RowMapper<ReviewReactionDto> reviewReactionMapper;

    @Override
    public Review add(ReviewSaveRequestDto reviewDto) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, new String[]{"review_id"});
            ps.setString(1, reviewDto.getContent());
            ps.setBoolean(2, reviewDto.getIsPositive());
            ps.setLong(3, reviewDto.getUserId());
            ps.setLong(4, reviewDto.getFilmId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return getById(id);
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Review update(ReviewUpdateRequestDto reviewDto) {
        jdbc.update(
                UPDATE_QUERY,
                reviewDto.getContent(),
                reviewDto.getIsPositive(),
                reviewDto.getUserId(),
                reviewDto.getFilmId(),
                reviewDto.getReviewId()
        );
        return getById(reviewDto.getReviewId());
    }

    @Override
    public void delete(Long id) {
        jdbc.update(DELETE_REVIEW_QUERY, id);
    }

    @Override
    public Review getById(Long id) {
        try {
            Review review = jdbc.queryForObject(FIND_BY_ID_QUERY.replace("@USEFUL_QUERY",USEFUL_QUERY),
                    mapper, id);
            matchReactions(Set.of(review));
            return  review;
        } catch (EmptyResultDataAccessException ignored) {
            log.error("getById. Review by id = {} not found", id);
            throw  new NotFoundException(String.format("Ревью с id = %d не найден", id));
        }
    }

    @Override
    public Collection<Review> getByFilmId(Integer id, int count) {
        Collection<Review> reviews = jdbc.query(FIND_BY_FILM_ID_QUERY.replace("@USEFUL_QUERY",USEFUL_QUERY),
                mapper, id, count);
        matchReactions(reviews);
        return  reviews;
    }

    @Override
    public Collection<Review> getTop(int count) {
        Collection<Review> reviews = jdbc.query(FIND_ALL_QUERY.replace("@USEFUL_QUERY",USEFUL_QUERY),
                mapper, count);
        matchReactions(reviews);
        return  reviews;
    }

    @Override
    public Review addLike(Long ReviewId, Integer userId) {
        jdbc.update(LIKE_DISLIKE_QUERY, ReviewId, userId, true, false);
        return getById(ReviewId);
    }

    @Override
    public Review removeLike(Long ReviewId, Integer userId) {
        jdbc.update(LIKE_DISLIKE_QUERY, ReviewId, userId, false, false);
        return getById(ReviewId);
    }

    @Override
    public Review addDislike(Long ReviewId, Integer userId) {
        jdbc.update(LIKE_DISLIKE_QUERY, ReviewId, userId, false, true);
        return getById(ReviewId);
    }

    @Override
    public Review removeDislike(Long ReviewId, Integer userId) {
        jdbc.update(LIKE_DISLIKE_QUERY, ReviewId, userId, false, false);
        return getById(ReviewId);
    }

    public Collection<ReviewReactionDto> getReactions(Set<Long> reviewsIds) {
        StringBuilder sb = new StringBuilder();
        boolean appendUnion = reviewsIds.size() > 1;
        for (Long reviewsId : reviewsIds) {
            sb.append("SELECT ");
            sb.append(reviewsId);
            sb.append(" as id");
            if (appendUnion) {
                sb.append(" Union ");
                appendUnion = false;
            }
        }
        return jdbc.query(REACTIONS_BY_REVIEW_ID_QUERY.replace("@REVIEWS_IDS",sb.toString()),
                reviewReactionMapper);
    }

    private void matchReactions(Collection<Review> reviews) {
        if (reviews.isEmpty()) {
            return;
        }
        Set<Long> reviewsIds = reviews.stream()
                        .map(Review::getId)
                .collect(Collectors.toSet());
        Collection<ReviewReactionDto> reactions = getReactions(reviewsIds);
        reviews.forEach(
                review -> {
                    review.getLikes().addAll(reactions.stream()
                            .filter(row ->
                                row.getReviewId().equals(review.getId()) && row.isLiked())
                                    .map(ReviewReactionDto::getUserId)
                            .collect(Collectors.toSet()));
                    review.getDislikes().addAll(reactions.stream()
                            .filter(row ->
                                    row.getReviewId().equals(review.getId()) && row.isDisliked())
                            .map(ReviewReactionDto::getUserId)
                            .collect(Collectors.toSet()));
                }
        );
    }
}

