package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(PostStatus postStatus);
    List<Post> findByPublished(boolean published);
    List<Post> findByTitleContainingAndAuthorAndCreatedAt(String title, String author, LocalDateTime createdAt);
    List<Post> findByAuthorAndStatus(String author, PostStatus status);
    List<Post> findByIdInAndPublishedTrue(List<Long> ids);
    List<Post> findByStatusAndAuthorNot(PostStatus status, String author);
    List<Post> findByAuthorAndStatusInOrderByUpdatedAtDesc(String author, Collection<PostStatus> statuses);
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.published = true
          AND (
                :kw IS NULL
             OR LOWER(p.title)   LIKE LOWER(CONCAT('%', :kw, '%'))
             OR LOWER(p.content) LIKE LOWER(CONCAT('%', :kw, '%'))
          )
          AND (
                :au IS NULL
             OR LOWER(p.author) LIKE LOWER(CONCAT('%', :au,  '%'))
          )
          AND ( :start IS NULL OR p.createdAt >= :start )
          AND ( :end   IS NULL OR p.createdAt <  :end   )
        ORDER BY p.createdAt DESC
    """)
    List<Post> filterPublished(
            @Param("kw")    String keyword,
            @Param("au")    String author,
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );}
