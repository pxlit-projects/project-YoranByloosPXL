package be.pxl.services.controller;

import be.pxl.services.TestBootApp;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@ContextConfiguration(classes = {TestBootApp.class, ReviewController.class})
class ReviewControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ReviewService reviewService;

    @Test
    void getReviewsByPostId_returnsOkAndListWithBody() throws Exception {
        var now = LocalDateTime.of(2025,1,1,12,0);
        var r = new Review();
        r.setId(1L); r.setPostId(5L); r.setReviewer("ann"); r.setComment("niet goed"); r.setCreatedAt(now);

        when(reviewService.getReviewsByPostId(5L)).thenReturn(List.of(r));

        mvc.perform(get("/api/reviews/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].postId").value(5))
                .andExpect(jsonPath("$[0].reviewer").value("ann"))
                .andExpect(jsonPath("$[0].comment").value("niet goed"))
                .andExpect(jsonPath("$[0].createdAt").exists());

        verify(reviewService).getReviewsByPostId(5L);
    }

    @Test
    void getReviewablePosts_assertsHeaderAndBody() throws Exception {
        var p = new Post();
        p.setId(10L); p.setTitle("Titel"); p.setAuthor("joris");

        when(reviewService.getReviewablePosts("ann")).thenReturn(List.of(p));

        mvc.perform(get("/api/reviews/reviewable").header("username","ann"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].author").value("joris"))
                .andExpect(jsonPath("$[0].title").value("Titel"));

        verify(reviewService).getReviewablePosts("ann");
    }

    @Test
    void getReviewablePosts_missingHeader_returns400() throws Exception {
        mvc.perform(get("/api/reviews/reviewable"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(reviewService);
    }

    @Test
    void rejectPostWithReview_returnsSavedReview_andVerifiesArgs() throws Exception {
        var saved = new Review();
        saved.setId(99L); saved.setPostId(7L); saved.setReviewer("ann"); saved.setComment("meh");

        when(reviewService.rejectPostWithReview(7L, "ann", "meh")).thenReturn(saved);

        mvc.perform(post("/api/reviews/7/reject")
                        .header("username","ann")
                        .param("comment","meh"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.postId").value(7))
                .andExpect(jsonPath("$.reviewer").value("ann"))
                .andExpect(jsonPath("$.comment").value("meh"));

        verify(reviewService).rejectPostWithReview(7L, "ann", "meh");
    }

    @Test
    void approvePost_callsService_andReturnsOk() throws Exception {
        mvc.perform(post("/api/reviews/123/approve"))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // lege body

        verify(reviewService).approvePost(123L);
    }
}
