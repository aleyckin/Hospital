package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.controllers.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
