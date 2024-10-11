package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewReactionDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewReactionMapper implements RowMapper<ReviewReactionDto> {

    @Override
    public ReviewReactionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewReactionDto reviewReaction = new ReviewReactionDto();
        reviewReaction.setReviewId(rs.getLong("review_id"));
        reviewReaction.setUserId(rs.getInt("user_id"));
        reviewReaction.setLiked(rs.getBoolean("is_liked"));
        reviewReaction.setDisliked(rs.getBoolean("is_disliked"));
        return reviewReaction;
    }
}
