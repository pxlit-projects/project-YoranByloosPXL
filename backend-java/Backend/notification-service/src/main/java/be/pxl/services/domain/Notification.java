package be.pxl.services.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    @Column(length = 1000)
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}
    public Notification(String recipient, String message) {
        this.recipient = recipient; this.message = message;
    }
    // getters/setters
    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
