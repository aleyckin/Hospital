package com.example.hospital.Hospital.controllers;

import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.enums.UserRole;

public class UserDTO {
    private long id;
    private String login;
    private String password;
    private UserRole role;
    private String mail;
    private String phoneNumber;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.mail = user.getMail();
        this.phoneNumber = user.getPhoneNumber();
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword()
    {
        return password;
    }

    public UserRole getRole() { return role; }

    public String getMail() {
        return mail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
