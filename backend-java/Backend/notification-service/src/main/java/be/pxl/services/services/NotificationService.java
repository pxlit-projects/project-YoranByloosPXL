package be.pxl.services.services;

import be.pxl.services.domain.Notification;
import be.pxl.services.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    public Notification createForUser(String recipient, String message) {
        return repo.save(new Notification(recipient, message));
    }

    @Transactional
    public List<Notification> getForUser(String user) {
        return repo.findByRecipientOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void deleteOneForUser(Long id, String user) {
        long affected = repo.deleteByIdAndRecipient(id, user);
        if (affected == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
    }

    @Transactional
    public void deleteAllForUser(String user) {
        repo.deleteAllByRecipient(user);
    }


}
