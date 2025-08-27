package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.messaging.PostModerationEvent;
import be.pxl.services.messaging.NotificationPublisher;
import be.pxl.services.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService implements IPostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final NotificationPublisher publisher;

    public PostService(PostRepository postRepository, NotificationPublisher publisher) {
        this.postRepository = postRepository;
        this.publisher = publisher;
    }

    @Override
    public List<Post> getPublishedPosts() {
        List<Post> posts = postRepository.findByPublished(true);
        log.info("Fetched published posts count={}", posts.size());
        return posts;
    }

    @Override
    public List<Post> getReviewablePosts(String reviewerUsername) {
        // Alleen INGEDIEND en NIET geschreven door de reviewer
        List<Post> posts = postRepository.findByStatusAndAuthorNot(PostStatus.INGEDIEND, reviewerUsername);
        log.info("Fetched reviewable posts for reviewer={} count={}", reviewerUsername, posts.size());
        return posts;
    }

    public List<Post> getDrafts(String username) {
        List<Post> posts = postRepository.findByAuthorAndStatus(username, PostStatus.CONCEPT);
        log.info("Fetched drafts for user={} count={}", username, posts.size());
        return posts;
    }

    @Override
    public Post createPost(String username, CreatePostDTO dto, boolean submitForReview) {
        log.info("Create post requested by user={} submitForReview={} title='{}'", username, submitForReview, dto.getTitle());
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getDescription());
        post.setAuthor(username);
        post.setStatus(submitForReview ? PostStatus.INGEDIEND : PostStatus.CONCEPT);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        log.debug("Post created id={} status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Override
    public Post updatePost(Long id, String username, UpdatePostDTO dto) {
        log.info("Update post requested id={} by user={}", id, username);
        Post post = postRepository.findById(id).orElseThrow(() -> {
            log.warn("Post not found id={}", id);
            return new IllegalArgumentException("Post not found");
        });
        if (!post.getAuthor().equals(username) || post.getStatus() != PostStatus.CONCEPT) {
            log.warn("Unauthorized update attempt id={} by user={} (status={})", id, username, post.getStatus());
            throw new IllegalArgumentException("You can only update your own drafts");
        }
        post.setTitle(dto.getTitle());
        post.setContent(dto.getDescription());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        log.debug("Post updated id={} status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Override
    public List<Post> filterPosts(String keyword, String author, LocalDateTime date) {
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String au = StringUtils.hasText(author)  ? author.trim()  : null;

        LocalDateTime start = null;
        LocalDateTime end   = null;
        if (date != null) {
            LocalDate d = date.toLocalDate();       // hele dag
            start = d.atStartOfDay();
            end   = start.plusDays(1);
        }

        List<Post> result = postRepository.filterPublished(kw, au, start, end);
        return result;
    }

    @Override
    public Post submitPost(Long id, String username) {
        log.info("Submit post requested id={} by user={}", id, username);
        Post post = postRepository.findById(id).orElseThrow(() -> {
            log.warn("Post not found id={}", id);
            return new IllegalArgumentException("Post not found");
        });
        if (!post.getAuthor().equals(username) || post.getStatus() != PostStatus.CONCEPT) {
            log.warn("Invalid submit attempt id={} by user={} (status={})", id, username, post.getStatus());
            throw new IllegalArgumentException("You can only submit your own drafts");
        }
        post.setStatus(PostStatus.INGEDIEND);
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        log.debug("Post submitted id={} newStatus={}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Override
    public Post publishPost(Long id, String username) {
        log.info("Publish post requested id={} by user={}", id, username);
        Post post = postRepository.findById(id).orElseThrow(() -> {
            log.warn("Post not found id={}", id);
            return new IllegalArgumentException("Post not found");
        });
        if (post.getStatus() != PostStatus.GOEDGEKEURD) {
            log.warn("Publish rejected id={} status={}", id, post.getStatus());
            throw new IllegalArgumentException("Post must be approved before publishing");
        }
        post.setPublished(true);
        post.setStatus(PostStatus.GEPUBLICEERD);
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        log.debug("Post published id={} status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Override
    public void updatePostStatus(Long id, String status) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

        post.setStatus(PostStatus.valueOf(status));
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        if (PostStatus.GEWEIGERD.name().equals(status) || PostStatus.GOEDGEKEURD.name().equals(status)) {
            PostModerationEvent evt = new PostModerationEvent(
                    post.getId(),
                    post.getAuthor(),
                    status,
                    null
            );
            publisher.publish(evt);
        }
    }

    public Post getById(Long id) {
        log.info("Get post by id requested id={}", id);
        return postRepository.findById(id).orElseThrow(() -> {
            log.warn("Post not found id={}", id);
            return new IllegalArgumentException("Post not found");
        });
    }

    public List<Post> getByIds(List<Long> ids) {
        log.info("Get posts by ids requested count={}", (ids == null ? 0 : ids.size()));
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Post> posts = postRepository.findByIdInAndPublishedTrue(ids);
        log.debug("Get posts by ids result count={}", posts.size());
        return posts;
    }

    @Override
    public List<Post> getMySubmissions(String username) {
        List<PostStatus> statuses = List.of(
                PostStatus.INGEDIEND,
                PostStatus.GOEDGEKEURD,
                PostStatus.GEWEIGERD
        );
        List<Post> posts = postRepository.findByAuthorAndStatusInOrderByUpdatedAtDesc(username, statuses);
        log.info("Fetched my submissions for user={} count={}", username, posts.size());
        return posts;
    }

    @Override
    public void moveToDraft(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().equals(username)) {
            throw new IllegalArgumentException("You can only move your own posts");
        }
        if (post.getStatus() != PostStatus.GEWEIGERD) {
            throw new IllegalArgumentException("Only rejected posts can be moved back to draft");
        }
        post.setStatus(PostStatus.CONCEPT);
        post.setPublished(false);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}
