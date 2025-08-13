package be.pxl.services.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import be.pxl.services.domain.Post;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final IPostService postService;
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestHeader("username") String username,
                                           @RequestBody CreatePostDTO dto,
                                           @RequestParam boolean submitForReview) {
        Post post = postService.createPost(username, dto, submitForReview);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @RequestHeader("username") String username,
                                           @RequestBody UpdatePostDTO dto) {
        Post post = postService.updatePost(id, username, dto);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<Post> submitPost(@PathVariable Long id, @RequestHeader("username") String username) {
        Post post = postService.submitPost(id, username);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id, @RequestHeader("username") String username) {
        Post post = postService.publishPost(id, username);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/published")
    public List<Post> getPublishedPosts() {
        log.info("Retrieving all published posts...");
        return postService.getPublishedPosts();
    }

    @GetMapping("/reviewable")
    public List<Post> getReviewablePosts() {
        return postService.getReviewablePosts();
    }

    @GetMapping("/drafts")
    public List<Post> getDrafts(@RequestHeader("username") String username) {
        return postService.getDrafts(username);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Post>> filterPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String date) {
        LocalDateTime dateTime = date != null ? LocalDateTime.parse(date) : null;
        List<Post> filteredPosts = postService.filterPosts(keyword, author, dateTime);
        return new ResponseEntity<>(filteredPosts, HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approvePost(@PathVariable Long id) {
        postService.updatePostStatus(id, "GOEDGEKEURD");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disapprove")
    public ResponseEntity<Void> disapprovePost(@PathVariable Long id) {
        postService.updatePostStatus(id, "GEWEIGERD");
        return ResponseEntity.noContent().build();
    }
}
