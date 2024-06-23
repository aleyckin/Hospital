package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.Util.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {
    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/simple-email/{user-email}")
    public ResponseEntity<String> sendSimpleEmail(@PathVariable("user-email") String email) {
        try {
            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for you!!");
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }

    @GetMapping(value = "/reset-password/{user-email}")
    public ResponseEntity<String> sendResetPasswordEmail(@PathVariable("user-email") String email) {
        try {
            String resetLink = "http://localhost:8080/reset-password?email=" + email;
            emailService.sendSimpleEmail(email, "Password Reset Request",
                    "To reset your password, click the link below:\n" + resetLink);
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox for password reset link", HttpStatus.OK);
    }

    @GetMapping("/reset")
    public ResponseEntity<String> showResetPasswordPage(@RequestParam("email") String email) {
        return new ResponseEntity<>("Ссылка для смены пароля корректна, перейдите на страницу сброса пароля", HttpStatus.OK);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email, @RequestParam("newPassword") String newPassword) {
        try {
            userService.updatePassword(email, newPassword);
            return new ResponseEntity<>("Пароль успешно изменен", HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error while resetting password..{}", e.getMessage());
            return new ResponseEntity<>("Ошибка при смене пароля", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
