package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.messaging.NotificationPublisher;
import be.pxl.services.messaging.PostModerationEvent;
import be.pxl.services.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository repo;
    @Mock private NotificationPublisher publisher;

    @InjectMocks private PostService service;

    private Post draft;

    @BeforeEach
    void setUp() {
        draft = Post.builder()
                .id(99L)
                .title("T")
                .content("C")
                .author("yoran")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .published(false)
                .status(PostStatus.CONCEPT)
                .build();
    }

    @Test
    void getPublishedPosts_delegatesToRepo() {
        when(repo.findByPublished(true)).thenReturn(List.of(draft));
        var list = service.getPublishedPosts();
        assertThat(list).hasSize(1);
        verify(repo).findByPublished(true);
    }

    @Test
    void getReviewablePosts_callsRepoWithSubmittedAndNotAuthor() {
        when(repo.findByStatusAndAuthorNot(PostStatus.INGEDIEND, "admin"))
                .thenReturn(List.of(draft));
        var list = service.getReviewablePosts("admin");
        assertThat(list).hasSize(1);
        verify(repo).findByStatusAndAuthorNot(PostStatus.INGEDIEND, "admin");
    }

    @Test
    void getDrafts_ok() {
        when(repo.findByAuthorAndStatus("yoran", PostStatus.CONCEPT)).thenReturn(List.of(draft));
        var list = service.getDrafts("yoran");
        assertThat(list).hasSize(1);
        verify(repo).findByAuthorAndStatus("yoran", PostStatus.CONCEPT);
    }

    @Test
    void createPost_setsAuthorAndStatus() {
        CreatePostDTO dto = new CreatePostDTO();
        dto.setTitle("Hello");
        dto.setDescription("World");

        when(repo.save(any(Post.class))).thenAnswer(a -> {
            Post p = a.getArgument(0);
            p.setId(1L);
            return p;
        });

        Post created = service.createPost("yoran", dto, true);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getAuthor()).isEqualTo("yoran");
        assertThat(created.getStatus()).isEqualTo(PostStatus.INGEDIEND);
        verify(repo).save(any(Post.class));
    }

    @Test
    void updatePost_ok_whenOwnDraft() {
        when(repo.findById(99L)).thenReturn(Optional.of(draft));
        when(repo.save(any(Post.class))).thenAnswer(a -> a.getArgument(0));

        UpdatePostDTO dto = new UpdatePostDTO();
        dto.setTitle("New");
        dto.setDescription("Desc");

        Post updated = service.updatePost(99L, "yoran", dto);

        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(updated.getContent()).isEqualTo("Desc");
        verify(repo).save(any(Post.class));
    }

    @Test
    void updatePost_throwsForOtherUser() {
        when(repo.findById(99L)).thenReturn(Optional.of(draft));
        UpdatePostDTO dto = new UpdatePostDTO();
        dto.setTitle("x");
        dto.setDescription("y");

        assertThatThrownBy(() -> service.updatePost(99L, "not-yoran", dto))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void filterPosts_buildsDayRangeAndDelegates() {
        LocalDateTime date = LocalDateTime.of(2025, 9, 25, 14, 0);

        service.filterPosts(" kw ", " au ", date);

        LocalDate startD = date.toLocalDate();
        LocalDateTime expectedStart = startD.atStartOfDay();
        LocalDateTime expectedEnd   = expectedStart.plusDays(1);

        verify(repo).filterPublished(eq("kw"), eq("au"), eq(expectedStart), eq(expectedEnd));
    }

    @Test
    void submitPost_transitionsToIngediend_forOwnDraft() {
        when(repo.findById(99L)).thenReturn(Optional.of(draft));
        when(repo.save(any(Post.class))).thenAnswer(a -> a.getArgument(0));

        Post submitted = service.submitPost(99L, "yoran");

        assertThat(submitted.getStatus()).isEqualTo(PostStatus.INGEDIEND);
        verify(repo).save(any(Post.class));
    }

    @Test
    void publishPost_requiresApproved() {
        // not approved yet -> throws
        Post p = Post.builder().id(1L).status(PostStatus.CONCEPT).author("a").build();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> service.publishPost(1L, "a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void publishPost_ok_whenApproved() {
        Post p = Post.builder().id(2L).status(PostStatus.GOEDGEKEURD).author("a").build();
        when(repo.findById(2L)).thenReturn(Optional.of(p));
        when(repo.save(any(Post.class))).thenAnswer(a -> a.getArgument(0));

        Post published = service.publishPost(2L, "a");

        assertThat(published.isPublished()).isTrue();
        assertThat(published.getStatus()).isEqualTo(PostStatus.GEPUBLICEERD);
        verify(repo).save(any(Post.class));
    }

    @Test
    void updatePostStatus_publishesEventOnTerminalStatuses() {
        Post p = Post.builder()
                .id(7L).author("yoran")
                .status(PostStatus.INGEDIEND)
                .build();
        when(repo.findById(7L)).thenReturn(Optional.of(p));

        service.updatePostStatus(7L, PostStatus.GOEDGEKEURD.name());

        ArgumentCaptor<PostModerationEvent> evt = ArgumentCaptor.forClass(PostModerationEvent.class);
        verify(publisher).publish(evt.capture());
        assertThat(evt.getValue().getPostId()).isEqualTo(7L);
        assertThat(evt.getValue().getAuthorUsername()).isEqualTo("yoran");
        assertThat(evt.getValue().getStatus()).isEqualTo(PostStatus.GOEDGEKEURD.name());
        verify(repo).save(any(Post.class));
    }

    @Test
    void getById_ok() {
        when(repo.findById(99L)).thenReturn(Optional.of(draft));
        Post found = service.getById(99L);
        assertThat(found.getId()).isEqualTo(99L);
    }

    @Test
    void getByIds_handlesNullAndEmpty() {
        assertThat(service.getByIds(null)).isEmpty();
        assertThat(service.getByIds(List.of())).isEmpty();
        verify(repo, never()).findByIdInAndPublishedTrue(anyList());
    }

    @Test
    void getMySubmissions_ok() {
        when(repo.findByAuthorAndStatusInOrderByUpdatedAtDesc(eq("yoran"), anyList()))
                .thenReturn(List.of(draft));
        var list = service.getMySubmissions("yoran");
        assertThat(list).hasSize(1);
        verify(repo).findByAuthorAndStatusInOrderByUpdatedAtDesc(eq("yoran"), anyList());
    }

    @Test
    void moveToDraft_onlyForAuthorAndRejected() {
        Post rejected = Post.builder()
                .id(33L).author("yoran").status(PostStatus.GEWEIGERD).published(false)
                .build();
        when(repo.findById(33L)).thenReturn(Optional.of(rejected));

        service.moveToDraft(33L, "yoran");
        verify(repo).save(any(Post.class));
    }
}
