package be.pxl.services.repository;

import be.pxl.services.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
    Optional<Notification> findByIdAndRecipient(Long id, String recipient);
    long deleteByIdAndRecipient(Long id, String recipient);
    void deleteAllByRecipient(String recipient);
}