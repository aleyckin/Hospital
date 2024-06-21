package com.example.hospital.Hospital.services;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Long id) {
        super(String.format("Doctor with id [%s] is not found", id));
    }
}
