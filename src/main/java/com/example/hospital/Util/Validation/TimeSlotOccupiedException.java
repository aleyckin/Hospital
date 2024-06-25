package com.example.hospital.Util.Validation;

public class TimeSlotOccupiedException extends RuntimeException {
    public TimeSlotOccupiedException(String message) {
        super(message);
    }
}
