package be.pxl.services.controller;

import be.pxl.services.domain.Notification;
import be.pxl.services.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public List<Notification> getForUser(@RequestParam("user") String user) {
        return service.getForUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id, @RequestParam("user") String user) {
        service.deleteOneForUser(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestParam("user") String user) {
        service.deleteAllForUser(user);
        return ResponseEntity.noContent().build();
    }
}
