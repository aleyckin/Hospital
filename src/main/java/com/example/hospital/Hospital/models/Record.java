package com.example.hospital.Hospital.models;

import com.example.hospital.Hospital.controllers.RecordDTO;
import com.example.hospital.Hospital.models.enums.Place;
import com.example.hospital.Hospital.models.enums.Status;
import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Double price;

    @Column
    @Enumerated(EnumType.STRING)
    private Place place;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Record() {

    }

    public Record(Double price, Place place, Status status)
    {
        this.price = price;
        this.place = place;
        this.status = status;
    }

    public Record(RecordDTO recordDTO) {
        this.price = recordDTO.getPrice();
        this.place = recordDTO.getPlace();
        this.status = recordDTO.getStatus();
    }


    public Long getId() { return id;}

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Place getPlace() { return place; }

    public Doctor getDoctor() { return doctor; }

    public User getUser() { return user; }


    public void setPlace(Place place) { this.place = place;}

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status;}

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setUser(User user) { this.user = user;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", price='" + price + '\'' +
                ", place='" + place + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
