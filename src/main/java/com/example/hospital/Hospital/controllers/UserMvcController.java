package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.VerificationToken;
import com.example.hospital.Hospital.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserMvcController {
    UserService userService;

    @Value("${frontend.url}")
    private String frontendURL;

    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            //model.addAttribute("message", "Invalid token");
            return "redirect:" + frontendURL + "/badUser";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            //model.addAttribute("message", "Token expired");
            return "redirect:" + frontendURL + "/badUser";
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        userService.deleteVerificationToken(token);
        return "redirect:" + frontendURL + "/login";
    }

    @PostMapping("/email/reset-password/{email}")
    public String sendResetPasswordEmail(@PathVariable String email) {
        User user = userService.findByMail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с указанным email не найден");
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);
        userService.sendPasswordResetEmail(user, token);
        return "Ссылка для смены пароля отправлена на вашу электронную почту";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token) {
        VerificationToken verificationToken = userService.getPasswordResetToken(token);
        if (verificationToken == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Token expired");
        }
        return "redirect:" + frontendURL + "/reset-password?token=" + token;
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        VerificationToken verificationToken = userService.getPasswordResetToken(token);
        if (verificationToken == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Token expired");
        }

        userService.updatePassword(user.getMail(), newPassword);
        userService.deleteVerificationToken(token);
        return "Пароль успешно изменен";
    }
}
