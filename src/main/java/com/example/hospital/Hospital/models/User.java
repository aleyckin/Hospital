package com.example.hospital.Hospital.models;

import com.example.hospital.Hospital.controllers.UserSignUpDTO;
import com.example.hospital.Hospital.models.enums.UserRole;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    @NotBlank
    @Size(min = 3, max = 64)
    private String login;

    @Column(nullable = false, length = 64)
    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    private UserRole role;

    @Column
    @NotBlank
    @Size(min = 6, max = 64)
    private String mail;

    @Column
    @NotBlank
    @Size(min = 6, max = 64)
    private String phoneNumber;

    public User() {
    }

    public User(String login, String password, String mail, String phoneNumber) {
        this(login, password, UserRole.USER, mail, phoneNumber);
    }

    public User(String login, String password, UserRole role, String mail, String phoneNumber) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.mail = mail;
        this.phoneNumber = phoneNumber;
    }

    public User(UserSignUpDTO userDto){
        this.login = userDto.getLogin();
        this.password = userDto.getPassword();
        this.role = UserRole.USER;
        this.mail = userDto.getMail();
        this.phoneNumber = userDto.getPhoneNumber();
    }


    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
