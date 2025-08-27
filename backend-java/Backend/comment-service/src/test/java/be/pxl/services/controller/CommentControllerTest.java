package be.pxl.services.controller;

import be.pxl.services.TestBootApp;
import be.pxl.services.domain.Comment;
import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;
import be.pxl.services.services.ICommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@ContextConfiguration(classes = {TestBootApp.class, CommentController.class})
class CommentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ICommentService commentService;

    @Test
    void getCommentsByPostId_returnsOkAndList() throws Exception {
        var now = LocalDateTime.of(2025,1,1,12,0);

        var c1 = new Comment();
        c1.setId(1L); c1.setPostId(5L); c1.setUsername("ann"); c1.setContent("hey"); c1.setCreatedAt(now); c1.setUpdatedAt(now);

        when(commentService.getCommentsByPostId(5L)).thenReturn(List.of(c1));

        mvc.perform(get("/api/comments/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].postId").value(5))
                .andExpect(jsonPath("$[0].username").value("ann"))
                .andExpect(jsonPath("$[0].content").value("hey"))
                .andExpect(jsonPath("$[0].createdAt").exists());

        verify(commentService).getCommentsByPostId(5L);
    }

    @Test
    void addComment_createsAndReturnsJson() throws Exception {
        var dto = new CreateCommentDTO();
        dto.setPostId(7L);
        dto.setContent("leuk!");

        var saved = new Comment();
        saved.setId(99L); saved.setPostId(7L); saved.setUsername("joris"); saved.setContent("leuk!");

        when(commentService.addComment(eq("joris"), any(CreateCommentDTO.class))).thenReturn(saved);

        mvc.perform(post("/api/comments")
                        .header("username", "joris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.postId").value(7))
                .andExpect(jsonPath("$.username").value("joris"))
                .andExpect(jsonPath("$.content").value("leuk!"));

        verify(commentService).addComment(eq("joris"), any(CreateCommentDTO.class));
    }

    @Test
    void updateComment_updatesAndReturnsJson() throws Exception {
        var dto = new UpdateCommentDTO();
        dto.setContent("edit");

        var updated = new Comment();
        updated.setId(5L);
        updated.setPostId(3L);
        updated.setUsername("ann");
        updated.setContent("edit");

        when(commentService.updateComment(eq(5L), eq("ann"), any(UpdateCommentDTO.class)))
                .thenReturn(updated);

        mvc.perform(put("/api/comments/5")
                        .header("username", "ann")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                // mag ook contentTypeCompatibleWith gebruiken als je iets toleranter wil:
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.content").value("edit"));

        verify(commentService).updateComment(eq(5L), eq("ann"), any(UpdateCommentDTO.class));
    }

    @Test
    void deleteComment_returns204_andInvokesService() throws Exception {
        mvc.perform(delete("/api/comments/42").header("username", "ann"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(commentService).deleteComment(42L, "ann");
    }

    @Test
    void addComment_missingHeader_returns400() throws Exception {
        var dto = new CreateCommentDTO();
        dto.setPostId(1L); dto.setContent("x");

        mvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }
}
