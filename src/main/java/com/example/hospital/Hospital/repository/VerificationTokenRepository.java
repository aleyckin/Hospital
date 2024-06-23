package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
