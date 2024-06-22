package com.example.hospital.Hospital.services;

import com.example.hospital.Hospital.controllers.UserDTO;
import com.example.hospital.Hospital.controllers.UserSignUpDTO;
import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.enums.UserRole;
import com.example.hospital.Hospital.repository.UserRepository;
import com.example.hospital.JWT.JwtException;
import com.example.hospital.JWT.JwtProvider;
import com.example.hospital.Util.Validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public User findByLogin(String login) {
        User example = userRepository.findOneByLoginIgnoreCase(login);
        return example;
    }
    public Page<User> findAllPages(int page, int size) {
        return userRepository.findAll(PageRequest.of(page - 1, size, Sort.by("id").ascending()));
    }

    @Transactional
    public User addUser(UserSignUpDTO userDto){
        final User user = new User(userDto);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User addUser(String login,
                        String password,
                        String passwordConfirm,
                        UserRole role,
                        String mail,
                        String phoneNumber){
        if (findByLogin(login) != null) {
            throw new ValidationException(String.format("User '%s' already exists", login));
        }
        if (!Objects.equals(password, passwordConfirm)) {
            throw new ValidationException("Passwords not equals");
        }
        final User user = new User(login, passwordEncoder.encode(password), role, mail, phoneNumber);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUser(Long id) {
        final Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(UserDTO userDTO) {
        final User currentUser = findByLogin(userDTO.getLogin());
        currentUser.setLogin(userDTO.getLogin());
        currentUser.setPassword(passwordEncoder.encode((userDTO.getPassword())));
        currentUser.setMail(userDTO.getMail());
        currentUser.setPhoneNumber(userDTO.getPhoneNumber());
        return userRepository.save(currentUser);
    }

    @Transactional
    public User updateUser(Long id, String login, String password) {
        if (!StringUtils.hasText(login) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("User name, login or password is null or empty");
        }
        final User currentUser = findUser(id);
        currentUser.setLogin(login);
        currentUser.setPassword(password);
        return userRepository.save(currentUser);
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User userEntity = findByLogin(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new org.springframework.security.core.userdetails.User(userEntity.getLogin(), userEntity.getPassword(), Collections.singleton(userEntity.getRole()));
    }

    public String loginAndGetToken(UserDTO userDTO) {
        final User user = findByLogin(userDTO.getLogin());
        if (user == null) {
        }
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
        }
        return jwtProvider.generateToken(user.getLogin());
    }

    public UserDetails loadUserByToken(String token) throws UsernameNotFoundException {
        if (!jwtProvider.isTokenValid(token)) {
            throw new JwtException("Bad token");
        }
        final String userLogin = jwtProvider.getLoginFromToken(token)
                .orElseThrow(() -> new JwtException("Token is not contain Login"));
        return loadUserByUsername(userLogin);
    }
}
