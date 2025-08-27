package be.pxl.services.controller;

import be.pxl.services.TestBootApp;
import be.pxl.services.domain.Bookmark;
import be.pxl.services.domain.Post;
import be.pxl.services.services.IBookmarkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookmarkController.class)
@ContextConfiguration(classes = {TestBootApp.class, BookmarkController.class})
class BookmarkControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean IBookmarkService bookmarkService;

    @Test
    void addBookmark_returnsSavedBookmarkAsJson() throws Exception {
        Bookmark saved = new Bookmark();
        saved.setId(100L); saved.setUsername("ann"); saved.setPostId(9L);

        when(bookmarkService.addBookmark("ann", 9L)).thenReturn(saved);

        mvc.perform(post("/api/bookmarks/9").header("username", "ann"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.postId").value(9))
                .andExpect(jsonPath("$.username").value("ann"));

        verify(bookmarkService).addBookmark("ann", 9L);
    }

    @Test
    void getMyBookmarkedPosts_returnsList() throws Exception {
        Post p = new Post();
        p.setId(1L); p.setTitle("t1");

        when(bookmarkService.getBookmarkedPosts("ann")).thenReturn(List.of(p));

        mvc.perform(get("/api/bookmarks").header("username", "ann"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("t1"));

        verify(bookmarkService).getBookmarkedPosts("ann");
    }

    @Test
    void removeBookmark_returns204() throws Exception {
        mvc.perform(delete("/api/bookmarks/7").header("username", "ann"))
                .andExpect(status().isNoContent());

        verify(bookmarkService).removeBookmark("ann", 7L);
    }
}
