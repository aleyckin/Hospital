package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.models.Doctor;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Specialization;

public class DoctorDTO {
    private Long id;
    private String name;
    private Place place;

    private Specialization specialization;

    public DoctorDTO() {}

    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.place = doctor.getPlace();
        this.specialization = doctor.getSpecialization();
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

    public Specialization getSpecialization() { return specialization; }

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setPlace(Place place) { this.place = place; }

    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }
}
