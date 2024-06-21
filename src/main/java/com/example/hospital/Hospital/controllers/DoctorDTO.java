package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.controllers.models.Doctor;
import com.example.hospital.Hospital.controllers.models.enums.Place;

public class DoctorDTO {
    private Long id;
    private String name;
    private Place place;

    public DoctorDTO() {}

    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.place = doctor.getPlace();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Place getPlace() {
        return place;
    }

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setPlace(Place place) { this.place = place; }
}
