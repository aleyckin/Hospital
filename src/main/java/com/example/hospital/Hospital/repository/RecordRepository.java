package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.models.Record;
import com.example.hospital.Hospital.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findRecordsByUserId(Long user_id);
}
