package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.models.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
