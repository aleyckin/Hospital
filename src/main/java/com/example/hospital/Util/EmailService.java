package com.example.hospital.Util;

import jakarta.mail.MessagingException;

import java.io.FileNotFoundException;

public interface EmailService {
    void sendSimpleEmail(String toAddress, String subject, String message);
}
