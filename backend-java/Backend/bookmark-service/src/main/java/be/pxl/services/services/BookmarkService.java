package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Bookmark;
import be.pxl.services.domain.Post; // DTO (geen JPA in deze service)
import be.pxl.services.domain.PostStatus;
import be.pxl.services.repository.BookmarkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService implements IBookmarkService {

    private static final Logger log = LoggerFactory.getLogger(BookmarkService.class);

    private final BookmarkRepository bookmarkRepository;
    private final PostClient postClient;

    public BookmarkService(BookmarkRepository bookmarkRepository, PostClient postClient) {
        this.bookmarkRepository = bookmarkRepository;
        this.postClient = postClient;
    }

    @Override
    @Transactional
    public Bookmark addBookmark(String username, Long postId) {
        log.info("AddBookmark requested user={} postId={}", username, postId);

        if (bookmarkRepository.existsByUsernameAndPostId(username, postId)) {
            throw new IllegalArgumentException("Bookmark already exists for this post");
        }

        Post post = postClient.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        if (post.getStatus() != PostStatus.GEPUBLICEERD) {
            throw new IllegalArgumentException("You can only bookmark published posts");
        }

        Bookmark b = new Bookmark();
        b.setUsername(username);
        b.setPostId(postId);
        b.setCreatedAt(LocalDateTime.now());
        Bookmark saved = bookmarkRepository.save(b);
        return saved;
    }

    @Override
    public List<Post> getBookmarkedPosts(String username) {
        var bookmarks = bookmarkRepository.findByUsernameOrderByCreatedAtDesc(username);
        log.info("GetBookmarkedPosts user={} count={}", username, bookmarks.size());
        if (bookmarks.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = bookmarks.stream().map(Bookmark::getPostId).collect(Collectors.toList());

        return postClient.getPostsByIds(ids);
    }

    @Override
    @Transactional
    public void removeBookmark(String username, Long postId) {
        log.info("RemoveBookmark requested user={} postId={}", username, postId);
        long deleted = bookmarkRepository.deleteByUsernameAndPostId(username, postId);
        if (deleted == 0) {
            throw new IllegalArgumentException("Bookmark not found");
        }
    }
}
