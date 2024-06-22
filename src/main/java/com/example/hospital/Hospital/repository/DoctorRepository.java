package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
