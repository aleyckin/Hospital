package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.models.Record;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordDTO {
    private Long id;

    private Double price;

    private Place place;

    private Status status;

    @JsonProperty("doctorId")
    private Long doctor_id;

    @JsonProperty("userId")
    private Long user_id;

    private String doctorName;

    private String userMail;


    public RecordDTO() {}

    public RecordDTO(Record record) {
        this.id = record.getId();
        this.price = record.getPrice();
        this.status = record.getStatus();
        this.doctor_id = record.getDoctor().getId();
        this.doctorName = record.getDoctor().getName();
        this.user_id = record.getUser().getId();
        this.userMail = record.getUser().getMail();
    }

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public Place getPlace() {
        return place;
    }

    public Status getStatus() {
        return status;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Long getDoctor_id() {
        return doctor_id;
    }

    public String getDoctorName() { return doctorName; }

    public String getUserMail() { return userMail; }

    public void setId(Long id) { this.id = id; }

    public void setPrice(Double price) { this.price = price; }

    public void setPlace(Place place) { this.place = place; }

    public void setStatus(Status status) { this.status = status; }

    public void setDoctorName(String name) { this.doctorName = name; }

    public void setDoctor_id(Long categoryId) {this.doctor_id = categoryId;}

    public void setUserMail(String mail) { this.userMail = userMail; }

    public void setUser_id(Long userId) { this.user_id = userId; }
}
