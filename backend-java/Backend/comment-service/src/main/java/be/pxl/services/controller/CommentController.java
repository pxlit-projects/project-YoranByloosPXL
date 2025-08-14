package be.pxl.services.controller;

import be.pxl.services.domain.Comment;
import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;
import be.pxl.services.services.ICommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        log.info("GetCommentsByPostId called postId={}", postId);
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestHeader("username") String username,
                                              @RequestBody CreateCommentDTO dto) {
        log.info("AddComment requested by user={} for postId={}", username, dto.getPostId());
        return ResponseEntity.ok(commentService.addComment(username, dto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId,
                                                 @RequestHeader("username") String username,
                                                 @RequestBody UpdateCommentDTO dto) {
        log.info("UpdateComment requested commentId={} by user={}", commentId, username);
        return ResponseEntity.ok(commentService.updateComment(commentId, username, dto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestHeader("username") String username) {
        log.info("DeleteComment requested commentId={} by user={}", commentId, username);
        commentService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }
}
