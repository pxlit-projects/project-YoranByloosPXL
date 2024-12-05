package be.pxl.services.services;

import be.pxl.services.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface IPostService {
    Post createPost(Post post);
    Post saveAsDraft(Post post);
    Post updatePost(Long id, Post updatedPost);
    List<Post> getPublishedPosts();
    List<Post> filterPosts(String keyword, String author, LocalDateTime date);
}
