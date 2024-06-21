package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.services.NotificationService;
import com.example.hospital.WebConfiguration;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(WebConfiguration.REST_API + "/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public NotificationDTO getNotification(@PathVariable Long id) {
        return new NotificationDTO(notificationService.findNotification(id));
    }

    @GetMapping
    public List<NotificationDTO> getNotifications() {
        return notificationService.findAllNotifications().stream()
                .map(NotificationDTO::new)
                .toList();
    }

    @PostMapping
    public NotificationDTO createNotification(@RequestBody @Valid NotificationDTO notificationDTO) throws IOException {
        return new NotificationDTO(notificationService.addNotification(notificationDTO));
    }

    @PutMapping("/{id}")
    public NotificationDTO updateNotification(@PathVariable Long id, @RequestBody @Valid NotificationDTO notificationDTO) {
        return new NotificationDTO(notificationService.updateNotification(id, notificationDTO));
    }

    @DeleteMapping("/{id}")
    public NotificationDTO deleteNotification(@PathVariable Long id) {
        return new NotificationDTO(notificationService.deleteNotification(id));
    }

    @DeleteMapping
    public void deleteAll() {
        notificationService.deleteAllNotifications();
    }
}
