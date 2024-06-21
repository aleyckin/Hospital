package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.controllers.models.User;
import com.example.hospital.Hospital.controllers.models.enums.UserRole;
import com.example.hospital.Hospital.services.UserService;
import com.example.hospital.Util.Validation.ValidationException;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    public static final String URL_LOGIN = "/jwt/login";
    public static final String URL_SIGN_UP = "/sign_up";
    public static final String URL_WHO_AM_I = "/who_am_i";
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

    @GetMapping("/{id}")
    @Secured({UserRole.AsString.ADMIN})
    public UserDTO getUser(@PathVariable Long id) {
        return new UserDTO(userService.findUser(id));
    }

    @GetMapping("/")
    @Secured({UserRole.AsString.ADMIN})
    public List<UserDTO> getUsers() {
        return userService.findAllUsers().stream()
                .map(UserDTO::new)
                .toList();
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@RequestBody @Valid UserDTO userDto){
        return new UserDTO(userService.updateUser(userDto));
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
