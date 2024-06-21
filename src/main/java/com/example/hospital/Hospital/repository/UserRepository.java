package com.example.hospital.Hospital.repository;

import com.example.hospital.Hospital.controllers.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByLoginIgnoreCase(String login);
}
