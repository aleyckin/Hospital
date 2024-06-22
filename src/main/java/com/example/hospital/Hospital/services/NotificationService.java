package com.example.hospital.Hospital.services;

import com.example.hospital.Hospital.controllers.NotificationDTO;
import com.example.hospital.Hospital.models.Notification;
import com.example.hospital.Hospital.models.enums.Status;
import com.example.hospital.Hospital.repository.NotificationRepository;
import com.example.hospital.Util.Validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ValidatorUtil validatorUtil;
    private final RecordService recordService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, ValidatorUtil validatorUtil, RecordService recordService) {
        this.notificationRepository = notificationRepository;
        this.validatorUtil = validatorUtil;
        this.recordService = recordService;
    }

    @Transactional
    public Notification addNotification(NotificationDTO notificationDTO) throws IOException {
        final Notification notification = new Notification(notificationDTO);
        notification.setRecord(recordService.findRecord(notificationDTO.getRecord_id()));
        validatorUtil.validate(notification);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification addNotification(Status status, String textInfo) {
        final Notification notification = new Notification(status, textInfo);
        validatorUtil.validate(notification);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Notification findNotification(Long id) {
        final Optional<Notification> post = notificationRepository.findById(id);
        return post.orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> findAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional
    public Notification updateNotification(Long id, NotificationDTO notificationDTO) {
        final Notification currentNotification = findNotification(id);
        currentNotification.setStatus(notificationDTO.getStatus());
        currentNotification.setTextInfo(notificationDTO.getTextInfo());
        currentNotification.setRecord(recordService.findRecord(notificationDTO.getRecord_id()));
        validatorUtil.validate(currentNotification);
        return notificationRepository.save(currentNotification);
    }

    @Transactional
    public Notification deleteNotification(Long id) {
        final Notification currentNotification = findNotification(id);
        notificationRepository.delete(currentNotification);
        return currentNotification;
    }

    @Transactional
    public void deleteAllNotifications() {
        notificationRepository.deleteAll();
    }
}
