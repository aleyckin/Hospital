package com.example.hospital.Hospital.controllers;

import org.springframework.context.MessageSource;
import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.VerificationToken;
import com.example.hospital.Hospital.models.enums.UserRole;
import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.Util.Validation.ValidationException;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
public class UserController {
    public static final String URL_LOGIN = "/jwt/login";
    public static final String URL_SIGN_UP = "/sign_up";
    public static final String URL_WHO_AM_I = "/who_am_i";

    public static final String URL_PROFILE = "/profile";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(URL_LOGIN)
    public String login(@RequestBody @Valid UserDTO userDTO) {
        return userService.loginAndGetToken(userDTO);
    }

    @GetMapping(URL_WHO_AM_I)
    public String role(@RequestParam("userLogin") String userLogin) {
        return userService.findByLogin(userLogin).getRole().name();
    }

    @PostMapping(URL_SIGN_UP)
    public String signUp(@RequestBody @Valid UserSignUpDTO userSignupDto) {
        try {
            final User user = userService.addUser(userSignupDto.getLogin(),
                    userSignupDto.getPassword(), userSignupDto.getPasswordConfirm(), UserRole.USER, userSignupDto.getMail(), userSignupDto.getPhoneNumber());
            return "Пользователь " + user.getLogin() + " создан.";
        } catch (ValidationException e) {
            return e.getMessage();
        }
    }

    @GetMapping("profile/{login}")
    @Secured({UserRole.AsString.ADMIN, UserRole.AsString.USER})
    public UserDTO getUser(@PathVariable String login) {
        User user = userService.findByLogin(login);
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setMail(user.getMail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        return userDto;
    }

    @GetMapping("/")
    @Secured({UserRole.AsString.ADMIN})
    public List<UserDTO> getUsers() {
        return userService.findAllUsers().stream()
                .map(UserDTO::new)
                .toList();
    }

    @PutMapping("profile/{login}")
    public UserDTO updateUser(@RequestBody @Valid UserDTO userDto){
        return new UserDTO(userService.updateUser(userDto));
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            model.addAttribute("message", "Invalid token");
            return "redirect:/badUser.html?lang=" + request.getLocale().getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", "Token expired");
            return "redirect:/badUser.html?lang=" + request.getLocale().getLanguage();
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        userService.deleteVerificationToken(token);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }

    @PostMapping("/email/reset-password/{email}")
    public String sendResetPasswordEmail(@PathVariable String email) {
        User user = userService.findByLogin(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с указанным email не найден");
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);
        userService.sendPasswordResetEmail(user, token);
        return "Ссылка для смены пароля отправлена на вашу электронную почту";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
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
        return "Пароль успешно изменен";
    }
}
