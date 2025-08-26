package be.pxl.services.repository;

import be.pxl.services.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUsernameAndPostId(String username, Long postId);
    List<Bookmark> findByUsernameOrderByCreatedAtDesc(String username);
    long deleteByUsernameAndPostId(String username, Long postId);}
