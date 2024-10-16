package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.ReviewSaveRequestDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    ReviewResponseDto createReviewResponse(Long id) {
        ReviewResponseDto review = new ReviewResponseDto();
        review.setReviewId(id);
        review.setPositive(true);
        review.setContent("ok");
        review.setFilmId(1);
        review.setUserId(1);
        return review;
    }

    @Test
    void findByFilmIdReturnAll() throws Exception {
        final  ReviewResponseDto review1 = createReviewResponse(1L);
        final  ReviewResponseDto review2 = createReviewResponse(2L);
        final  ReviewResponseDto review3 = createReviewResponse(3L);
        final List<ReviewResponseDto> reviews = Arrays.asList(review1, review2, review3);
        when(reviewService.findAll(10)).thenReturn(reviews);
        String response = this.mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(reviews), response);
    }

    @Test
    void findByFilmIdReturnById() throws Exception {
        final  ReviewResponseDto review1 = createReviewResponse(1L);
        final  ReviewResponseDto review2 = createReviewResponse(2L);
        final  ReviewResponseDto review3 = createReviewResponse(3L);
        final List<ReviewResponseDto> reviews = Arrays.asList(review1, review2, review3);
        when(reviewService.findByFilmId(1,10)).thenReturn(reviews);
        String response = this.mockMvc.perform(get("/reviews?filmId=1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(reviews), response);
    }

    @Test
    void findByByIdReturnOne() throws Exception {
        final  ReviewResponseDto review = createReviewResponse(1L);
        when(reviewService.findById(1L)).thenReturn(review);
        String response = this.mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(review), response);
    }

    @Test
    void createNewReviewIsOK() throws Exception {
        ReviewSaveRequestDto review = new ReviewSaveRequestDto();
        review.setIsPositive(true);
        review.setContent("ok");
        review.setFilmId(1);
        review.setUserId(1);
        ReviewResponseDto responseDto = createReviewResponse(1L);
        when(reviewService.save(any(ReviewSaveRequestDto.class))).thenReturn(responseDto);
        String response = this.mockMvc.perform(post("/reviews")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(responseDto), response);
    }

    @Test
    void addLikeReturnReview() throws Exception {
        ReviewResponseDto responseDto = createReviewResponse(1L);
        when(reviewService.like(1L, 1)).thenReturn(responseDto);
        String response = this.mockMvc.perform(put("/reviews/1/like/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(responseDto), response);
    }
}