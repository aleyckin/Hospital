package com.example.hospital.Hospital.controllers;

import com.example.hospital.WebConfiguration;
import org.springframework.context.MessageSource;
import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.VerificationToken;
import com.example.hospital.Hospital.models.enums.UserRole;
import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.Util.Validation.ValidationException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<String> login(@RequestBody @Valid UserDTO userDTO) {
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

    @GetMapping(value = { URL_PROFILE + "/{login}", WebConfiguration.REST_API + "/user/current"})
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

    @PutMapping(URL_PROFILE + "/{login}")
    public UserDTO updateUser(@RequestBody @Valid UserDTO userDto){
        return new UserDTO(userService.updateUser(userDto));
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
