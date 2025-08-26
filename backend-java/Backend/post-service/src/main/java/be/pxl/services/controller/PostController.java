package be.pxl.services.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import be.pxl.services.domain.Post;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;
import be.pxl.services.services.IPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        log.info("CreatePost requested by user={} submitForReview={}", username, submitForReview);
        Post post = postService.createPost(username, dto, submitForReview);
        log.debug("CreatePost succeeded id={}", post.getId());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @RequestHeader("username") String username,
                                           @RequestBody UpdatePostDTO dto) {
        log.info("UpdatePost requested id={} by user={}", id, username);
        Post post = postService.updatePost(id, username, dto);
        log.debug("UpdatePost done id={} status={}", id, post.getStatus());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<Post> submitPost(@PathVariable Long id, @RequestHeader("username") String username) {
        log.info("SubmitPost requested id={} by user={}", id, username);
        Post post = postService.submitPost(id, username);
        log.debug("SubmitPost done id={} status={}", id, post.getStatus());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id, @RequestHeader("username") String username) {
        log.info("PublishPost requested id={} by user={}", id, username);
        Post post = postService.publishPost(id, username);
        log.debug("PublishPost done id={} status={}", id, post.getStatus());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}/to-draft")
    public ResponseEntity<Void> moveToDraft(@PathVariable Long id,
                                            @RequestHeader("username") String username) {
        log.info("MoveToDraft requested id={} by user={}", id, username);
        postService.moveToDraft(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/published")
    public List<Post> getPublishedPosts() {
        log.info("GetPublishedPosts called");
        return postService.getPublishedPosts();
    }

    @GetMapping("/reviewable")
    public List<Post> getReviewablePosts(@RequestHeader("username") String username) {
        log.info("GetReviewablePosts called reviewer={}", username);
        return postService.getReviewablePosts(username);
    }

    @GetMapping("/drafts")
    public List<Post> getDrafts(@RequestHeader("username") String username) {
        log.info("GetDrafts called for user={}", username);
        return postService.getDrafts(username);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Post>> filterPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String date
    ) {
        log.info("FilterPosts called keyword={} author={} date={}", keyword, author, date);

        LocalDateTime dateTime = parseFlexible(date);
        List<Post> filteredPosts = postService.filterPosts(keyword, author, dateTime);

        log.debug("FilterPosts result count={}", filteredPosts.size());
        return ResponseEntity.ok(filteredPosts);
    }

    private LocalDateTime parseFlexible(String raw) {
        if (!StringUtils.hasText(raw)) return null;
        try {
            // formaat: 2025-09-25T00:00:00
            return LocalDateTime.parse(raw);
        } catch (Exception ignore) {
        }
        try {
            // formaat: 2025-09-25
            return LocalDate.parse(raw).atStartOfDay();
        } catch (Exception ignore) {
        }
        log.warn("Unsupported date format '{}', ignoring date filter", raw);
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        log.info("GetPostById called id={}", id);
        Post post = postService.getById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/by-ids")
    public List<Post> getPostsByIds(@RequestParam List<Long> ids) {
        log.info("GetPostsByIds called count={}", ids.size());
        return postService.getByIds(ids);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approvePost(@PathVariable Long id) {
        log.info("ApprovePost requested id={}", id);
        postService.updatePostStatus(id, "GOEDGEKEURD");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disapprove")
    public ResponseEntity<Void> disapprovePost(@PathVariable Long id) {
        log.info("DisapprovePost requested id={}", id);
        postService.updatePostStatus(id, "GEWEIGERD");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/submissions")
    public List<Post> getMySubmissions(@RequestHeader("username") String username) {
        log.info("GetMySubmissions called user={}", username);
        return postService.getMySubmissions(username);
    }
}
