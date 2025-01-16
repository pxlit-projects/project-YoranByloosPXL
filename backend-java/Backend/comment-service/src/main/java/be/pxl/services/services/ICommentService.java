package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.dto.CreateCommentDTO;
import be.pxl.services.dto.UpdateCommentDTO;

import java.util.List;

public interface ICommentService {
    List<Comment> getCommentsByPostId(Long postId);
    Comment addComment(String username, CreateCommentDTO dto);
    Comment updateComment(Long commentId, String username, UpdateCommentDTO dto);
    void deleteComment(Long commentId, String username);
}
