package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Bookmark;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.repository.BookmarkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock BookmarkRepository bookmarkRepository;
    @Mock PostClient postClient;

    @InjectMocks BookmarkService service;

    private Post post(long id, PostStatus status) {
        Post p = new Post(); // DTO in deze service
        p.setId(id);
        p.setStatus(status);
        return p;
    }

    @Test
    void addBookmark_ok_whenNotExists_andPostPublished() {
        when(bookmarkRepository.existsByUsernameAndPostId("ann", 10L)).thenReturn(false);
        when(postClient.getPostById(10L)).thenReturn(post(10L, PostStatus.GEPUBLICEERD));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(i -> {
            Bookmark b = i.getArgument(0);
            b.setId(99L);
            return b;
        });

        Bookmark saved = service.addBookmark("ann", 10L);

        assertThat(saved.getId()).isEqualTo(99L);
        assertThat(saved.getUsername()).isEqualTo("ann");
        assertThat(saved.getPostId()).isEqualTo(10L);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void addBookmark_throws_whenAlreadyExists() {
        when(bookmarkRepository.existsByUsernameAndPostId("ann", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.addBookmark("ann", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already");
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void addBookmark_throws_whenPostNotPublished() {
        when(bookmarkRepository.existsByUsernameAndPostId("ann", 10L)).thenReturn(false);
        when(postClient.getPostById(10L)).thenReturn(post(10L, PostStatus.GOEDGEKEURD));

        assertThatThrownBy(() -> service.addBookmark("ann", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("only bookmark published");
    }

    @Test
    void getBookmarkedPosts_empty_whenNoBookmarks() {
        when(bookmarkRepository.findByUsernameOrderByCreatedAtDesc("ann")).thenReturn(List.of());

        List<Post> result = service.getBookmarkedPosts("ann");

        assertThat(result).isEmpty();
        verify(postClient, never()).getPostsByIds(anyList());
    }

    @Test
    void getBookmarkedPosts_callsPostClientWithIds() {
        Bookmark b1 = new Bookmark();
        b1.setId(1L); b1.setUsername("ann"); b1.setPostId(10L); b1.setCreatedAt(LocalDateTime.now());
        Bookmark b2 = new Bookmark();
        b2.setId(2L); b2.setUsername("ann"); b2.setPostId(11L); b2.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        when(bookmarkRepository.findByUsernameOrderByCreatedAtDesc("ann")).thenReturn(List.of(b1, b2));
        when(postClient.getPostsByIds(List.of(10L, 11L))).thenReturn(List.of(post(10L, PostStatus.GEPUBLICEERD)));

        List<Post> result = service.getBookmarkedPosts("ann");

        assertThat(result).hasSize(1);
        verify(postClient).getPostsByIds(List.of(10L, 11L));
    }

    @Test
    void removeBookmark_ok_whenDeleted() {
        when(bookmarkRepository.deleteByUsernameAndPostId("ann", 10L)).thenReturn(1L);

        service.removeBookmark("ann", 10L);

        verify(bookmarkRepository).deleteByUsernameAndPostId("ann", 10L);
    }

    @Test
    void removeBookmark_throws_whenNotFound() {
        when(bookmarkRepository.deleteByUsernameAndPostId("ann", 10L)).thenReturn(0L);

        assertThatThrownBy(() -> service.removeBookmark("ann", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
