package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.models.Record;
import com.example.hospital.Hospital.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findRecordsByUserId(Long user_id);

    @Query("SELECT r FROM Record r WHERE r.doctor.id = :doctorId AND ((r.startTime < :endTime AND r.endTime > :startTime) OR (r.startTime = :startTime AND r.endTime = :endTime))")
    List<Record> findRecordsByDoctorIdAndTimeRange(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT r FROM Record r " +
            "WHERE r.doctor.id = :doctorId " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime) " +
            "OR (r.startTime >= :startTime AND r.startTime < :endTime) " +
            "OR (r.endTime > :startTime AND r.endTime <= :endTime)) " +
            "AND r.id <> :recordId")
    List<Record> findRecordsByDoctorIdAndTimeRangeExceptCurrent(@Param("recordId") Long recordId,
                                                                @Param("doctorId") Long doctorId,
                                                                @Param("startTime") LocalDateTime startTime,
                                                                @Param("endTime") LocalDateTime endTime);
}
