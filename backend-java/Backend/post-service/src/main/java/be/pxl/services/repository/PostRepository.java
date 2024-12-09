package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPublished(boolean published);
    List<Post> findByTitleContainingAndAuthorAndCreatedAt(String title, String author, LocalDateTime createdAt);
}
