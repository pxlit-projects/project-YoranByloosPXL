package be.pxl.services.services;

import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.domain.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService implements ICommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        log.info("Fetched comments for postId={} count={}", postId, comments.size());
        return comments;
    }

    public Comment addComment(String username, CreateCommentDTO dto) {
        log.info("Add comment requested by user={} postId={}", username, dto.getPostId());
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUsername(username);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String username, UpdateCommentDTO dto) {
        log.info("Update comment requested commentId={} by user={}", commentId, username);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Comment not found commentId={}", commentId);
            return new IllegalArgumentException("Comment not found");
        });
        if (!comment.getUsername().equals(username)) {
            log.warn("Unauthorized comment update attempt commentId={} by user={}", commentId, username);
            throw new IllegalArgumentException("You can only update your own comments");
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        log.info("Delete comment requested commentId={} by user={}", commentId, username);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("Comment not found commentId={}", commentId);
            return new IllegalArgumentException("Comment not found");
        });
        if (!comment.getUsername().equals(username)) {
            log.warn("Unauthorized comment delete attempt commentId={} by user={}", commentId, username);
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}
