package com.example.hospital.Hospital.services;

public class NotificationNotFoundException extends RuntimeException{
    public NotificationNotFoundException(Long id) {
        super(String.format("Notification with id [%s] is not found", id));
    }
}
