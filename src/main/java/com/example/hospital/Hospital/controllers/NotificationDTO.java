package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.controllers.models.Notification;
import com.example.hospital.Hospital.controllers.models.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationDTO {
    private Long id;

    private String textInfo;

    private Status status;

    @JsonProperty("recordId")
    private Long record_id;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.status = notification.getStatus();
        this.record_id = notification.getRecord().getId();
    }

    public Long getId() {
        return id;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public Status getStatus() {
        return status;
    }

    public Long getRecord_id() {
        return record_id;
    }

    public void setId(Long id) { this.id = id; }

    public void setTextInfo(String textInfo) { this.textInfo = textInfo; }

    public void setStatus(Status status) { this.status = status; }

    public void setRecord_id(Long recordId) { this.record_id = recordId; }
}
