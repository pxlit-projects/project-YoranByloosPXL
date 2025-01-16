package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService implements IPostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getPublishedPosts() {
        return postRepository.findByPublished(true);
    }

    public List<Post> getReviewablePosts() {
        return postRepository.findByStatus(PostStatus.INGEDIEND);
    }

    public List<Post> getDrafts(String username) {
        return postRepository.findByAuthorAndStatus(username, PostStatus.CONCEPT);
    }

    @Override
    public Post createPost(String username, CreatePostDTO dto, boolean submitForReview) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getDescription());
        post.setAuthor(username);
        post.setStatus(submitForReview ? PostStatus.INGEDIEND : PostStatus.CONCEPT);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, String username, UpdatePostDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().equals(username) || post.getStatus() != PostStatus.CONCEPT) {
            throw new IllegalArgumentException("You can only update your own drafts");
        }
        post.setTitle(dto.getTitle());
        post.setContent(dto.getDescription());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public List<Post> filterPosts(String keyword, String author, LocalDateTime date) {
        return postRepository.findByTitleContainingAndAuthorAndCreatedAt(keyword, author, date);
    }

    @Override
    public Post submitPost(Long id, String username) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().equals(username) || post.getStatus() != PostStatus.CONCEPT) {
            throw new IllegalArgumentException("You can only submit your own drafts");
        }
        post.setStatus(PostStatus.INGEDIEND);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public Post publishPost(Long id, String username) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (post.getStatus() != PostStatus.GOEDGEKEURD) {
            throw new IllegalArgumentException("Post must be approved before publishing");
        }
        post.setPublished(true);
        post.setStatus(PostStatus.GEPUBLICEERD);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Override
    public void updatePostStatus(Long id, String status) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));

        post.setStatus(PostStatus.valueOf(status));
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}