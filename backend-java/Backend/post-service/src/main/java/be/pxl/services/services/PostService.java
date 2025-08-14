package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService implements IPostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getPublishedPosts() {
        List<Post> posts = postRepository.findByPublished(true);
        log.info("Fetched published posts count={}", posts.size());
        return posts;
    }

    public List<Post> getReviewablePosts() {
        List<Post> posts = postRepository.findByStatus(PostStatus.INGEDIEND);
        log.info("Fetched reviewable posts count={}", posts.size());
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
        log.info("Filter posts keyword='{}' author='{}' date={}", keyword, author, date);
        List<Post> result = postRepository.findByTitleContainingAndAuthorAndCreatedAt(keyword, author, date);
        log.debug("Filter result count={}", result.size());
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
        log.info("Update post status requested id={} newStatus={}", id, status);
        Post post = postRepository.findById(id).orElseThrow(() -> {
            log.warn("Post not found id={}", id);
            return new IllegalArgumentException("Post not found with id: " + id);
        });
        post.setStatus(PostStatus.valueOf(status));
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        log.debug("Post status updated id={} status={}", id, post.getStatus());
    }
}
