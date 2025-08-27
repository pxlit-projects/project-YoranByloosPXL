// be.pxl.services.messaging.PostModerationEvent
package be.pxl.services.messaging;

import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;

public class PostModerationEvent {
    private Long postId;
    private String authorUsername;
    private String status;     // APPROVED / REJECTED
    private String reason;     // optional

    public PostModerationEvent() {} // Jackson needs no-arg ctor

    public PostModerationEvent(Long postId, String authorUsername, String status, String reason) {
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.status = status;
        this.reason = reason;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
