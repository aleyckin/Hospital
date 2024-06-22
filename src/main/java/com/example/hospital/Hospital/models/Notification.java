package com.example.hospital.Hospital.models;

import com.example.hospital.Hospital.controllers.NotificationDTO;
import com.example.hospital.Hospital.models.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    @Size(min = 3, max = 64)
    private String textInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    public Notification() {

    }

    public Notification(Status status, String textInfo)
    {
        this.status = status;
        this.textInfo = textInfo;
    }

    public Notification(NotificationDTO notificationDTO) {
        this.status = notificationDTO.getStatus();
        this.textInfo = notificationDTO.getTextInfo();
    }

    public Long getId() { return id;}

    public String getTextInfo() { return textInfo; }

    public void setTextInfo(String textInfo) { this.textInfo = textInfo; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status;}

    public Record getRecord() { return record; }

    public void setRecord(Record record) { this.record = record; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification notification = (Notification) o;
        return Objects.equals(id, notification.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", textInfo='" + textInfo + '\'' +
                '}';
    }
}
