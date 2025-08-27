package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;
import be.pxl.services.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository repository;

    @InjectMocks
    CommentService service;

    @Captor
    ArgumentCaptor<Comment> commentCaptor;

    @BeforeEach
    void resetMocks() { reset(repository); }

    @Test
    void getCommentsByPostId_returnsListFromRepo() {
        when(repository.findByPostId(7L)).thenReturn(List.of(new Comment()));
        assertThat(service.getCommentsByPostId(7L)).hasSize(1);
        verify(repository).findByPostId(7L);
    }

    @Test
    void addComment_setsFieldsAndSaves() {
        var dto = new CreateCommentDTO();
        dto.setPostId(5L);
        dto.setContent("hoi");

        when(repository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        var saved = service.addComment("ann", dto);

        assertThat(saved.getPostId()).isEqualTo(5L);
        assertThat(saved.getUsername()).isEqualTo("ann");
        assertThat(saved.getContent()).isEqualTo("hoi");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        verify(repository).save(commentCaptor.capture());
        assertThat(commentCaptor.getValue().getUsername()).isEqualTo("ann");
    }

    @Test
    void updateComment_ok_whenOwner() {
        var existing = new Comment();
        existing.setId(11L);
        existing.setUsername("ann");
        existing.setContent("old");

        when(repository.findById(11L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = new UpdateCommentDTO();
        dto.setContent("new");

        var updated = service.updateComment(11L, "ann", dto);
        assertThat(updated.getContent()).isEqualTo("new");
        assertThat(updated.getUpdatedAt()).isNotNull();

        verify(repository).save(existing);
    }

    @Test
    void updateComment_throws_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        var dto = new UpdateCommentDTO();
        dto.setContent("x");
        assertThatThrownBy(() -> service.updateComment(99L, "ann", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    void updateComment_throws_whenDifferentUser() {
        var existing = new Comment();
        existing.setId(12L);
        existing.setUsername("bob");
        when(repository.findById(12L)).thenReturn(Optional.of(existing));

        var dto = new UpdateCommentDTO();
        dto.setContent("x");

        assertThatThrownBy(() -> service.updateComment(12L, "ann", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("update your own");
    }

    @Test
    void deleteComment_ok_whenOwner() {
        var existing = new Comment();
        existing.setId(33L);
        existing.setUsername("ann");
        when(repository.findById(33L)).thenReturn(Optional.of(existing));

        service.deleteComment(33L, "ann");
        verify(repository).delete(existing);
    }

    @Test
    void deleteComment_throws_whenNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteComment(77L, "ann"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    void deleteComment_throws_whenDifferentUser() {
        var existing = new Comment();
        existing.setId(44L);
        existing.setUsername("bob");
        when(repository.findById(44L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.deleteComment(44L, "ann"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("delete your own");
        verify(repository, never()).delete(any());
    }
}
