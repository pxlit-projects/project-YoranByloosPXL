package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;

    @Override
    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.INGEDIEND);
        post.setPublished(false);
        postRepository.save(post);
        return post;
    }

    @Override
    public Post saveAsDraft(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.CONCEPT);
        post.setPublished(false);
        postRepository.save(post);
        return post;
    }

    @Override
    public Post updatePost(Long id, Post updatedPost) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setAuthor(updatedPost.getAuthor());
            post.setUpdatedAt(updatedPost.getUpdatedAt());
            return postRepository.save(post);
        }
        throw new RuntimeException("Post not found with id: " + id);
    }

    @Override
    public List<Post> getPublishedPosts() {
        return postRepository.findAllByPublished(true);
    }

    @Override
    public List<Post> filterPosts(String keyword, String author, LocalDateTime date) {
        return postRepository.findByTitleContainingAndAuthorAndCreatedAt(keyword, author, date);
    }
}