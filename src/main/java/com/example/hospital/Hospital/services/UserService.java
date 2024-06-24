package com.example.hospital.Hospital.services;

import com.example.hospital.Hospital.controllers.UserDTO;
import com.example.hospital.Hospital.controllers.UserSignUpDTO;
import com.example.hospital.Hospital.models.User;
import com.example.hospital.Hospital.models.VerificationToken;
import com.example.hospital.Hospital.models.enums.UserRole;
import com.example.hospital.Hospital.repository.UserRepository;
import com.example.hospital.Hospital.repository.VerificationTokenRepository;
import com.example.hospital.JWT.JwtException;
import com.example.hospital.JWT.JwtProvider;
import com.example.hospital.Util.Validation.ValidationException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Console;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    private JavaMailSender mailSender;

    private VerificationTokenRepository tokenRepository;

    @Value("${frontend.url}")
    private String frontendURL;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, VerificationTokenRepository token) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.tokenRepository = token;
    }

    public User findByLogin(String login) {
        return userRepository.findOneByLoginIgnoreCase(login);
    }

    public User findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public Page<User> findAllPages(int page, int size) {
        return userRepository.findAll(PageRequest.of(page - 1, size, Sort.by("id").ascending()));
    }

    private void sendVerificationEmail(User user, String token) {
        String recipientAddress = user.getMail();
        String subject = "Подтверждение регистрации";
        String confirmationUrl = frontendURL + "/registrationConfirm?token=" + token;
        String message = "<p>Для подтверждения регистрации, пожалуйста, перейдите по ссылке:</p>"
                + "<a href=\"" + confirmationUrl + "\">Подтвердить регистрацию</a>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            mimeMessage.setContent(message, "text/html; charset=utf-8");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
        mailSender.send(mimeMessage);
    }

    @Transactional
    public User addUser(String login,
                        String password,
                        String passwordConfirm,
                        UserRole role,
                        String mail,
                        String phoneNumber) {
        if (findByLogin(login) != null) {
            throw new ValidationException(String.format("User '%s' already exists", login));
        }
        if (findByMail(mail) != null) {
            throw new ValidationException(String.format("User '%s' already exists", mail));
        }
        if (!Objects.equals(password, passwordConfirm)) {
            throw new ValidationException("Passwords not equals");
        }
        final User user = new User(login, passwordEncoder.encode(password), role, mail, phoneNumber);
        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        if (role != UserRole.ADMIN) {
            String token = UUID.randomUUID().toString();
            createVerificationToken(savedUser, token);
            sendVerificationEmail(savedUser, token);
        } else {
            savedUser.setEnabled(true);
            userRepository.save(savedUser);
        }
        return savedUser;
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

        User existingUserWithMail = userRepository.findByMail(userDTO.getMail());
        if (existingUserWithMail != null && !existingUserWithMail.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

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

    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByMail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Пользователь с указанным email не найден");
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        final User userEntity = findByLogin(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new org.springframework.security.core.userdetails.User(userEntity.getLogin(), userEntity.getPassword(), userEntity.getEnabled(), accountNonExpired, credentialsNonExpired,
                accountNonLocked, Collections.singleton(userEntity.getRole()));
    }

    public ResponseEntity<String> loginAndGetToken(UserDTO userDTO) {
        final User user = findByLogin(userDTO.getLogin());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
        }
        if (!user.getEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not activated. Please check your email for activation link.");
        }
        return ResponseEntity.ok(jwtProvider.generateToken(user.getLogin()));
    }

    public UserDetails loadUserByToken(String token) throws UsernameNotFoundException {
        if (!jwtProvider.isTokenValid(token)) {
            throw new JwtException("Bad token");
        }
        final String userLogin = jwtProvider.getLoginFromToken(token)
                .orElseThrow(() -> new JwtException("Token is not contain Login"));
        return loadUserByUsername(userLogin);
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    public void createVerificationToken(User user, String token) {
        if (hasActiveVerificationToken(user)) {
            throw new IllegalStateException("Пользователь уже имеет активный токен");
        }

        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    public boolean hasActiveVerificationToken(User user) {
        VerificationToken existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            Calendar cal = Calendar.getInstance();
            return (existingToken.getExpiryDate().getTime() - cal.getTime().getTime()) > 0;
        }
        return false;
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteVerificationToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }
    }

    @Transactional
    public void createPasswordResetToken(User user, String token) {
        try {
            if (hasActiveVerificationToken(user)) {
                throw new IllegalStateException("Пользователь уже имеет активный токен");
            }
            VerificationToken myToken = new VerificationToken(token, user);
            tokenRepository.save(myToken);
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public void sendPasswordResetEmail(User user, String token) {
        String recipientAddress = user.getMail();
        String subject = "Сброс пароля";
        String confirmationUrl = frontendURL + "/reset-password?token=" + token;
        String message = "Для сброса пароля, пожалуйста, перейдите по <a href=\"" + confirmationUrl + "\">этой ссылке</a>.";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(message, true); // true indicates the text is HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle error
        }
    }

    @Transactional(readOnly = true)
    public VerificationToken getPasswordResetToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
