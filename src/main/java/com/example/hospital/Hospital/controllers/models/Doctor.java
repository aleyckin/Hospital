package com.example.hospital.Hospital.controllers.models;

import com.example.hospital.Hospital.controllers.DoctorDTO;
import com.example.hospital.Hospital.controllers.models.enums.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Size(min = 3, max = 64)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Place place;

    public Doctor() {

    }

    public Doctor(String name, Place place) {
        this.name = name;
        this.place = place;
    }

    public Doctor(DoctorDTO doctorDTO) {
        this.name = doctorDTO.getName();
        this.place = doctorDTO.getPlace();
    }

    public Long getId() { return id;}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Place getPlace() { return place; }

    public void setPlace(Place place) { this.place = place;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
