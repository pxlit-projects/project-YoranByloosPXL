package be.pxl.services.services;

import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment addComment(String username, CreateCommentDTO dto) {
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUsername(username);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, String username, UpdateCommentDTO dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only update your own comments");
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}
