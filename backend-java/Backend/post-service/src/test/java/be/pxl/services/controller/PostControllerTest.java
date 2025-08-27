package be.pxl.services.controller;

import be.pxl.services.domain.Post;
import be.pxl.services.services.IPostService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Resource
    private MockMvc mvc;

    @MockBean
    private IPostService postService;

    @Test
    void getPublished_ok() throws Exception {
        when(postService.getPublishedPosts()).thenReturn(List.of(new Post()));
        mvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(postService).getPublishedPosts();
    }

    @Test
    void approve_callsServiceWithApproved() throws Exception {
        mvc.perform(put("/api/posts/{id}/approve", 42L))
                .andExpect(status().isNoContent());
        verify(postService).updatePostStatus(42L, "GOEDGEKEURD");
    }

    @Test
    void disapprove_callsServiceWithRejected() throws Exception {
        mvc.perform(put("/api/posts/{id}/disapprove", 5L))
                .andExpect(status().isNoContent());
        verify(postService).updatePostStatus(5L, "GEWEIGERD");
    }

    @Test
    void filter_acceptsDateOnly_yyyyMMdd() throws Exception {
        mvc.perform(get("/api/posts/filter")
                        .param("keyword", "post")
                        .param("author", "yo")
                        .param("date", "2025-09-25"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(postService).filterPosts(eq("post"), eq("yo"), captor.capture());
        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue().toLocalDate().toString()).isEqualTo("2025-09-25");
    }

    @Test
    void filter_acceptsIsoDateTime() throws Exception {
        mvc.perform(get("/api/posts/filter")
                        .param("keyword", "abc")
                        .param("date", "2025-09-01T00:00:00"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(postService).filterPosts(eq("abc"), isNull(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDateTime.of(2025, 9, 1, 0, 0));
    }

    @Test
    void filter_invalidDate_passesNull() throws Exception {
        mvc.perform(get("/api/posts/filter")
                        .param("keyword", "x")
                        .param("date", "nonsense"))
                .andExpect(status().isOk());

        verify(postService).filterPosts(eq("x"), isNull(), isNull());
    }


    @Test
    void getByIds_ok() throws Exception {
        when(postService.getByIds(List.of(1L,2L))).thenReturn(List.of());
        mvc.perform(get("/api/posts/by-ids").param("ids","1,2"))
                .andExpect(status().isOk());
        verify(postService).getByIds(List.of(1L,2L));
    }
}
