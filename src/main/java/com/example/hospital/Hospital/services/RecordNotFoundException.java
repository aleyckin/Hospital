package com.example.hospital.Hospital.services;

public class RecordNotFoundException extends RuntimeException{
    public RecordNotFoundException(Long id) {
        super(String.format("Record with id [%s] is not found", id));
    }
}
