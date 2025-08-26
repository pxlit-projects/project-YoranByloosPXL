package be.pxl.services.controller;

import be.pxl.services.domain.Bookmark;
import be.pxl.services.domain.Post;
import be.pxl.services.services.IBookmarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private static final Logger log = LoggerFactory.getLogger(BookmarkController.class);
    private final IBookmarkService bookmarkService;

    public BookmarkController(IBookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Bookmark> addBookmark(@PathVariable Long postId,
                                                @RequestHeader("username") String username) {
        log.info("AddBookmark requested by user={} postId={}", username, postId);
        Bookmark saved = bookmarkService.addBookmark(username, postId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getMyBookmarkedPosts(@RequestHeader("username") String username) {
        log.info("GetMyBookmarkedPosts called for user={}", username);
        List<Post> posts = bookmarkService.getBookmarkedPosts(username);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long postId,
                                               @RequestHeader("username") String username) {
        log.info("RemoveBookmark requested by user={} postId={}", username, postId);
        bookmarkService.removeBookmark(username, postId);
        return ResponseEntity.noContent().build();
    }
}
