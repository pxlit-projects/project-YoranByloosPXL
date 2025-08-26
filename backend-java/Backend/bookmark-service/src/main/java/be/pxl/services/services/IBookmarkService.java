package be.pxl.services.services;

import be.pxl.services.domain.Bookmark;
import be.pxl.services.domain.Post;

import java.util.List;

public interface IBookmarkService {
    Bookmark addBookmark(String username, Long postId);
    List<Post> getBookmarkedPosts(String username);
    void removeBookmark(String username, Long postId);
}
