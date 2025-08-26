package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.dto.CreatePostDTO;
import be.pxl.services.dto.UpdatePostDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IPostService {
    Post createPost(String username, CreatePostDTO dto, boolean submitForReview);

    Post updatePost(Long id, String username, UpdatePostDTO dto);

    Post submitPost(Long id, String username);

    Post publishPost(Long id, String username);

    List<Post> getPublishedPosts();

    List<Post> getReviewablePosts(String reviewerUsername);

    List<Post> getDrafts(String username);

    List<Post> filterPosts(String keyword, String author, LocalDateTime date);

    void updatePostStatus(Long id, String status);

    Post getById(Long id);

    List<Post> getByIds(List<Long> ids);

    List<Post> getMySubmissions(String username);

    void moveToDraft(Long id, String username);
}
